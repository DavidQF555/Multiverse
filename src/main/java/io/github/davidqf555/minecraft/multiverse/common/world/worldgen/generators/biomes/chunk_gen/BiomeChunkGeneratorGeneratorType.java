package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeChunkGeneratorGeneratorType<T extends BiomeChunkGeneratorGenerator<?>> extends ForgeRegistryEntry<BiomeChunkGeneratorGeneratorType<?>> {

    private final Codec<T> codec;

    public BiomeChunkGeneratorGeneratorType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<T> getCodec() {
        return codec;
    }

}
