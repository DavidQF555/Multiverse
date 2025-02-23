package multiverse.common.world.worldgen.generators;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class DimensionGeneratorType<T extends DimensionGenerator> extends ForgeRegistryEntry<DimensionGeneratorType<?>> {

    private final Codec<T> codec;

    public DimensionGeneratorType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<T> getCodec() {
        return codec;
    }

}
