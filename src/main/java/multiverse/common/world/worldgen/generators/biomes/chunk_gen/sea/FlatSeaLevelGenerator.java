package multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea;

import com.mojang.serialization.MapCodec;
import multiverse.common.util.IntRange;
import multiverse.common.world.worldgen.sea.FlatFluidPicker;
import multiverse.common.world.worldgen.sea.SerializableFluidPicker;
import multiverse.registration.custom.SeaLevelGeneratorTypeRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class FlatSeaLevelGenerator implements SeaLevelGenerator {

    public static final MapCodec<FlatSeaLevelGenerator> CODEC = IntRange.CODEC.fieldOf("range").xmap(FlatSeaLevelGenerator::new, sea -> sea.range);
    private final IntRange range;

    protected FlatSeaLevelGenerator(IntRange range) {
        this.range = range;
    }

    public static FlatSeaLevelGenerator of(int min, int max) {
        return new FlatSeaLevelGenerator(IntRange.of(min, max));
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState fluid, RandomSource random) {
        return new FlatFluidPicker(range.getRandom(random), fluid);
    }

    @Override
    public MapCodec<? extends FlatSeaLevelGenerator> getCodec() {
        return SeaLevelGeneratorTypeRegistry.FLAT.get();
    }

}
