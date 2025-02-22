package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeDimensionGeneratorType extends ForgeRegistryEntry<BiomeDimensionGeneratorType> {

    private final Codec<? extends BiomeDimensionGenerator> codec;

    public BiomeDimensionGeneratorType(Codec<? extends BiomeDimensionGenerator> codec) {
        this.codec = codec;
    }

    public Codec<? extends BiomeDimensionGenerator> getCodec() {
        return codec;
    }

}
