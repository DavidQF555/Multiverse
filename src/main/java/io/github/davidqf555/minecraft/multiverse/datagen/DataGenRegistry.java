package io.github.davidqf555.minecraft.multiverse.datagen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.NoiseSettingsRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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
        Map<ResourceLocation, NoiseGeneratorSettings> values = new HashMap<>();
        NoiseSettingsRegistry.SETTINGS.forEach((loc, val) -> values.put(loc, val.settings()));
        event.getGenerator().addProvider(new CodecDataProvider<>(event.getGenerator(), Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings.DIRECT_CODEC, values));
    }

}
