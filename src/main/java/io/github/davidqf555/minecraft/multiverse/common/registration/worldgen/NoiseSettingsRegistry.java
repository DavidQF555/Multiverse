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

    public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "overworld/normal"));
    public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD_ISLANDS = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "overworld/islands"));
    public static final ResourceKey<NoiseGeneratorSettings> NETHER = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "nether/normal"));
    public static final ResourceKey<NoiseGeneratorSettings> NETHER_ROOFED = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "nether/roofed"));
    public static final ResourceKey<NoiseGeneratorSettings> NETHER_ISLANDS = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "nether/islands"));
    public static final ResourceKey<NoiseGeneratorSettings> END = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "end/normal"));
    public static final ResourceKey<NoiseGeneratorSettings> END_ROOFED = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "end/roofed"));
    public static final ResourceKey<NoiseGeneratorSettings> END_ISLANDS = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "end/islands"));

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
