package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.noise_settings;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeNoiseGeneratorSettingsProviderType<T extends BiomeNoiseGeneratorSettingsProvider> extends ForgeRegistryEntry<BiomeNoiseGeneratorSettingsProviderType<?>> {

    private final Codec<T> codec;

    public BiomeNoiseGeneratorSettingsProviderType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<T> getCodec() {
        return codec;
    }

}
