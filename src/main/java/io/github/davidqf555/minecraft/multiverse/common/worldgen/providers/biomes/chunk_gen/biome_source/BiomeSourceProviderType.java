package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.biome_source;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeSourceProviderType<T extends BiomeSourceProvider<?>> extends ForgeRegistryEntry<BiomeSourceProviderType<?>> {

    private final Codec<T> codec;

    public BiomeSourceProviderType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<? extends T> getCodec() {
        return codec;
    }

}
