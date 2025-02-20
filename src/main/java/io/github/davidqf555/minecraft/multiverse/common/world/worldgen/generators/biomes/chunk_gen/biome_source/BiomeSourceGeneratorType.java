package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeSourceGeneratorType<T extends BiomeSourceGenerator<?>> extends ForgeRegistryEntry<BiomeSourceGeneratorType<?>> {

    private final Codec<T> codec;

    public BiomeSourceGeneratorType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<? extends T> getCodec() {
        return codec;
    }

}
