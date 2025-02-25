package multiverse.common.events;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.GeneratorHelper;
import multiverse.common.world.worldgen.generators.GeneratorSettings;
import multiverse.common.world.worldgen.generators.ShapeDimensionGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GeneratorEvents {

    private static final Logger LOGGER = LogUtils.getLogger();

    private GeneratorEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerAboutToStart(ServerAboutToStartEvent event) throws IOException {
        MinecraftServer server = event.getServer();
        GeneratorSettings.load(server, new ResourceLocation(Multiverse.MOD_ID, "generator.json"));
        int count = GeneratorSettings.getSettings().count();
        LOGGER.info("Configured " + count + " generated multiverse dimensions");
        if (count > 0) {
            ShapeDimensionGenerator provider = ShapeDimensionGenerator.load(server, new ResourceLocation(Multiverse.MOD_ID, "shapes.json"));
            WritableRegistry<LevelStem> registry = (WritableRegistry<LevelStem>) server.getWorldData().worldGenSettings().dimensions();
            long seed = server.getWorldData().worldGenSettings().seed();
            for (int i = 1; i <= count; i++) {
                ResourceKey<LevelStem> key = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, GeneratorHelper.getResourceLocation(i));
                if (!registry.containsKey(key)) {
                    registry.register(key, provider.createDimension(server.registryAccess(), seed, i), Lifecycle.experimental());
                    LOGGER.debug("Generated and registered multiverse dimension: " + key.location());
                }
            }
        }
    }

}
