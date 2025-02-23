package multiverse.datagen;

import multiverse.common.Multiverse;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenRegistry {

    private DataGenRegistry() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        if (event.includeServer()) {
            DataGenerator gen = event.getGenerator();
            Map<ResourceLocation, NoiseGeneratorSettings> noise = new HashMap<>();
            NoiseSettingsRegistry.SETTINGS.forEach((loc, val) -> noise.put(loc, val.settings()));
            gen.addProvider(new CodecDataProvider<>(gen, Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings.DIRECT_CODEC, noise));
            gen.addProvider(new CodecDataProvider<>(gen, Registry.DIMENSION_TYPE_REGISTRY, DimensionType.DIRECT_CODEC, DimensionTypeRegistry.TYPES));
        }
    }

}
