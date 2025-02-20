package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.dim_type;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeDimensionTypeGeneratorType extends ForgeRegistryEntry<BiomeDimensionTypeGeneratorType> {

    private final Codec<? extends BiomeDimensionTypeProvider> codec;

    public BiomeDimensionTypeGeneratorType(Codec<? extends BiomeDimensionTypeProvider> codec) {
        this.codec = codec;
    }

    public Codec<? extends BiomeDimensionTypeProvider> getCodec() {
        return codec;
    }

}
