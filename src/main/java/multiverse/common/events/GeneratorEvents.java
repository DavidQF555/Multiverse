package multiverse.common.events;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.ShapesReader;
import multiverse.common.world.worldgen.generators.GeneratorHelper;
import multiverse.common.world.worldgen.generators.GeneratorSettingsReader;
import multiverse.common.world.worldgen.generators.ShapeDimensionGenerator;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.List;
import java.util.stream.IntStream;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GeneratorEvents {

    private static final Logger LOGGER = LogUtils.getLogger();

    private GeneratorEvents() {
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerAboutToStartHigh(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        GeneratorSettingsReader settings = new GeneratorSettingsReader(new ResourceLocation(Multiverse.MOD_ID, "generator.json"));
        settings.load(server);
        int count = settings.getDimensionsCount();
        LOGGER.info("Configured " + count + " generated multiverse dimensions");
        RegistryAccess access = server.registryAccess();
        MappedRegistry<LevelStem> registry = (MappedRegistry<LevelStem>) access.registryOrThrow(Registries.LEVEL_STEM);
        List<Pair<Integer, ResourceKey<LevelStem>>> missing = IntStream.range(1, count + 1)
                .mapToObj(i -> Pair.of(i, GeneratorHelper.getResourceLocation(i)))
                .filter(pair -> !registry.containsKey(pair.getSecond()))
                .map(pair -> pair.mapSecond(loc -> ResourceKey.create(Registries.LEVEL_STEM, loc)))
                .toList();
        if (!missing.isEmpty()) {
            ShapesReader shapes = new ShapesReader(new ResourceLocation(Multiverse.MOD_ID, "shapes.json"));
            shapes.load(server);
            ShapeDimensionGenerator provider = new ShapeDimensionGenerator(shapes.getShapes());
            registry.unfreeze();
            long seed = server.getWorldData().worldGenOptions().seed();
            for (Pair<Integer, ResourceKey<LevelStem>> pair : missing) {
                ResourceKey<LevelStem> key = pair.getSecond();
                registry.register(key, provider.createDimension(access, seed, pair.getFirst()), Lifecycle.experimental());
                LOGGER.debug("Generated and registered multiverse dimension: " + key.location());
            }
            registry.freeze();
        }
    }

}
