package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeDimensionTypeProviderType extends ForgeRegistryEntry<BiomeDimensionTypeProviderType> {

    private final Codec<? extends BiomeDimensionTypeProvider> codec;

    public BiomeDimensionTypeProviderType(Codec<? extends BiomeDimensionTypeProvider> codec) {
        this.codec = codec;
    }

    public Codec<? extends BiomeDimensionTypeProvider> getCodec() {
        return codec;
    }

}
