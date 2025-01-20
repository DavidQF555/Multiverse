package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.noise_settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.BiomeFieldProvider;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeNoiseGeneratorSettingsProviderRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeNoiseGeneratorSettingsProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.function.Function;

public interface BiomeNoiseGeneratorSettingsProvider extends BiomeFieldProvider<Holder<NoiseGeneratorSettings>> {

    Codec<BiomeNoiseGeneratorSettingsProvider> DIRECT_CODEC = Codec.lazyInitialized(() -> BiomeNoiseGeneratorSettingsProviderTypeRegistry.getRegistry().byNameCodec().dispatch(BiomeNoiseGeneratorSettingsProvider::getCodec, Function.identity()));
    Codec<Holder<BiomeNoiseGeneratorSettingsProvider>> CODEC = RegistryFileCodec.create(BiomeNoiseGeneratorSettingsProviderRegistry.LOCATION, DIRECT_CODEC);

    MapCodec<? extends BiomeNoiseGeneratorSettingsProvider> getCodec();

}
