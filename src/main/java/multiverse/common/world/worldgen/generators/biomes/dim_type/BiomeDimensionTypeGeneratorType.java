package multiverse.common.world.worldgen.generators.biomes.dim_type;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeDimensionTypeGeneratorType extends ForgeRegistryEntry<BiomeDimensionTypeGeneratorType> {

    private final Codec<? extends BiomeDimensionTypeGenerator> codec;

    public BiomeDimensionTypeGeneratorType(Codec<? extends BiomeDimensionTypeGenerator> codec) {
        this.codec = codec;
    }

    public Codec<? extends BiomeDimensionTypeGenerator> getCodec() {
        return codec;
    }

}
