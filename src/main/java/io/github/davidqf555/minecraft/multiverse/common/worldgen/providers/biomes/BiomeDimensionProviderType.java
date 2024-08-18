package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeDimensionProviderType extends ForgeRegistryEntry<BiomeDimensionProviderType> {

    private final Codec<? extends BiomeDimensionProvider> codec;

    public BiomeDimensionProviderType(Codec<? extends BiomeDimensionProvider> codec) {
        this.codec = codec;
    }

    public Codec<? extends BiomeDimensionProvider> getCodec() {
        return codec;
    }

}
