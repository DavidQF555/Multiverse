package io.github.davidqf555.minecraft.multiverse.common.registration.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.MultiverseType;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class NoiseSettingsRegistry {

    public static final ResourceKey<NoiseGeneratorSettings> NORMAL = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "normal"));
    public static final ResourceKey<NoiseGeneratorSettings> ISLANDS = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "islands"));
    public static final ResourceKey<NoiseGeneratorSettings> ROOFED = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "roofed"));

    private NoiseSettingsRegistry() {
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (MultiverseType type : MultiverseType.values()) {
                Registry.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, type.getNoiseSettingsKey(), type.getNoiseSettings());
            }
        });
    }

}
