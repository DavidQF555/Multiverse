package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeChunkGeneratorProviderType<T extends BiomeChunkGeneratorProvider<?>> extends ForgeRegistryEntry<BiomeChunkGeneratorProviderType<?>> {

    private final Codec<T> codec;

    public BiomeChunkGeneratorProviderType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<T> getCodec() {
        return codec;
    }

}
