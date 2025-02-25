package multiverse.common.events;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import multiverse.common.Multiverse;
import multiverse.common.util.MultiverseConfig;
import multiverse.common.world.worldgen.biomes.TerraBlenderBiomes;
import multiverse.common.world.worldgen.generators.GeneratorHelper;
import multiverse.common.world.worldgen.generators.GeneratorSettings;
import multiverse.common.world.worldgen.generators.ShapeDimensionGenerator;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class GeneratorEvents {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TERRABLENDER = "terrablender";

    private GeneratorEvents() {
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onServerAboutToStart(ServerAboutToStartEvent event) throws IOException {
        if (ModList.get().isLoaded(TERRABLENDER)) {
            MultiverseConfig.setBiomesManager(new TerraBlenderBiomes(event.getServer().registryAccess().registryOrThrow(Registries.BIOME)));
        }
        MinecraftServer server = event.getServer();
        GeneratorSettings.load(server, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "generator.json"));
        int count = GeneratorSettings.getSettings().count();
        LOGGER.info("Configured " + count + " generated multiverse dimensions");
        RegistryAccess access = server.registryAccess();
        MappedRegistry<LevelStem> registry = (MappedRegistry<LevelStem>) access.registryOrThrow(Registries.LEVEL_STEM);
        List<Pair<Integer, ResourceKey<LevelStem>>> missing = IntStream.range(1, count + 1)
                .mapToObj(i -> Pair.of(i, GeneratorHelper.getResourceLocation(i)))
                .filter(pair -> !registry.containsKey(pair.getSecond()))
                .map(pair -> pair.mapSecond(loc -> ResourceKey.create(Registries.LEVEL_STEM, loc)))
                .toList();
        if (!missing.isEmpty()) {
            ShapeDimensionGenerator provider = ShapeDimensionGenerator.load(server, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "shapes.json"));
            registry.unfreeze();
            long seed = server.getWorldData().worldGenOptions().seed();
            RegistrationInfo info = new RegistrationInfo(Optional.empty(), Lifecycle.experimental());
            for (Pair<Integer, ResourceKey<LevelStem>> pair : missing) {
                ResourceKey<LevelStem> key = pair.getSecond();
                registry.register(key, provider.createDimension(access, seed, pair.getFirst()), info);
                LOGGER.debug("Generated and registered multiverse dimension: " + key.location());
            }
            registry.freeze();
        }
    }

}
