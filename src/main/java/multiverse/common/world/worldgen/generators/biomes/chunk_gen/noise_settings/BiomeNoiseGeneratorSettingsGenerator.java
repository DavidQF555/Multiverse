package multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings;

import com.mojang.serialization.Codec;
import multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import multiverse.registration.custom.generator.biomes.BiomeNoiseGeneratorSettingsGeneratorRegistry;
import multiverse.registration.custom.generator.biomes.BiomeNoiseGeneratorSettingsGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.function.Function;

public interface BiomeNoiseGeneratorSettingsGenerator extends BiomeFieldGenerator<Holder<NoiseGeneratorSettings>> {

    Codec<BiomeNoiseGeneratorSettingsGenerator> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeNoiseGeneratorSettingsGeneratorTypeRegistry.getRegistry().getCodec().dispatch(BiomeNoiseGeneratorSettingsGenerator::getCodec, Function.identity()));
    Codec<Holder<BiomeNoiseGeneratorSettingsGenerator>> CODEC = RegistryFileCodec.create(BiomeNoiseGeneratorSettingsGeneratorRegistry.LOCATION, DIRECT_CODEC);

    Codec<? extends BiomeNoiseGeneratorSettingsGenerator> getCodec();

}
