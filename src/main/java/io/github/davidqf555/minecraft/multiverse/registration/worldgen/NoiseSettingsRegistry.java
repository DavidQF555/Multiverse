package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.IMultiverseNoiseGeneratorSettings;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.registries.DeferredRegister;

public final class NoiseSettingsRegistry {

    public static final DeferredRegister<NoiseGeneratorSettings> SETTINGS = DeferredRegister.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, Multiverse.MOD_ID);

    static {
        for (MultiverseShape type : MultiverseShape.values()) {
            for (MultiverseType biome : MultiverseType.values()) {
                SETTINGS.register(type.getLocation(biome), () -> {
                    NoiseGeneratorSettings settings = type.createNoiseSettings(biome, BuiltinRegistries.DENSITY_FUNCTION);
                    ((IMultiverseNoiseGeneratorSettings) (Object) settings).setSettings(type, biome);
                    return settings;
                });
            }
        }
    }

    private NoiseSettingsRegistry() {
    }

}
