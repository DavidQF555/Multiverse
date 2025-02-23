package multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SeaLevelGeneratorType<T extends SeaLevelGenerator> extends ForgeRegistryEntry<SeaLevelGeneratorType<?>> {

    private final Codec<T> codec;

    public SeaLevelGeneratorType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<T> getCodec() {
        return codec;
    }

}
