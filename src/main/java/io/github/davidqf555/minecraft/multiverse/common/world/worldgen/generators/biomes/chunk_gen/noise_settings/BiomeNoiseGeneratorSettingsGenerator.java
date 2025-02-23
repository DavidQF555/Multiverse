package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeNoiseGeneratorSettingsGeneratorRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeNoiseGeneratorSettingsGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.function.Function;

public interface BiomeNoiseGeneratorSettingsGenerator extends BiomeFieldGenerator<Holder<NoiseGeneratorSettings>> {

    Codec<BiomeNoiseGeneratorSettingsGenerator> DIRECT_CODEC = Codec.lazyInitialized(() -> BiomeNoiseGeneratorSettingsGeneratorTypeRegistry.getRegistry().byNameCodec().dispatch(BiomeNoiseGeneratorSettingsGenerator::getCodec, Function.identity()));
    Codec<Holder<BiomeNoiseGeneratorSettingsGenerator>> CODEC = RegistryFileCodec.create(BiomeNoiseGeneratorSettingsGeneratorRegistry.LOCATION, DIRECT_CODEC);

    MapCodec<? extends BiomeNoiseGeneratorSettingsGenerator> getCodec();

}
