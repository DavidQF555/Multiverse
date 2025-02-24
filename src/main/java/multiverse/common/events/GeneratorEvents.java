package multiverse.common.events;

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
        if (count > 0) {
            ShapesReader shapes = new ShapesReader(new ResourceLocation(Multiverse.MOD_ID, "shapes.json"));
            shapes.load(server);
            ShapeDimensionGenerator provider = new ShapeDimensionGenerator(shapes.getShapes());
            RegistryAccess access = server.registryAccess();
            MappedRegistry<LevelStem> registry = (MappedRegistry<LevelStem>) access.registryOrThrow(Registries.LEVEL_STEM);
            registry.unfreeze();
            long seed = server.getWorldData().worldGenOptions().seed();
            for (int i = 1; i <= count; i++) {
                ResourceKey<LevelStem> key = ResourceKey.create(Registries.LEVEL_STEM, GeneratorHelper.getResourceLocation(i));
                if (!registry.containsKey(key)) {
                    registry.register(key, provider.createDimension(access, seed, i), Lifecycle.experimental());
                    LOGGER.debug("Generated and registered multiverse dimension: " + key.location());
                }
            }
            registry.freeze();
        }
    }

}
