package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeDimensionGeneratorType extends ForgeRegistryEntry<BiomeDimensionGeneratorType> {

    private final Codec<? extends BiomeDimensionProvider> codec;

    public BiomeDimensionGeneratorType(Codec<? extends BiomeDimensionProvider> codec) {
        this.codec = codec;
    }

    public Codec<? extends BiomeDimensionProvider> getCodec() {
        return codec;
    }

}
