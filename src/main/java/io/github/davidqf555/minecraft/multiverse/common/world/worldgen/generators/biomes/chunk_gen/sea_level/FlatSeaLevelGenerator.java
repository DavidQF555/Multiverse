package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.util.IntRange;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea_level.fluid_pickers.FlatFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelProviderTypeRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSource;

public class FlatSeaLevelGenerator extends SeaLevelGenerator {

    public static final Codec<FlatSeaLevelGenerator> CODEC = IntRange.CODEC.fieldOf("range").xmap(FlatSeaLevelGenerator::new, sea -> sea.range).codec();
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
    public SeaLevelGeneratorType<?> getType() {
        return SeaLevelProviderTypeRegistry.FLAT.get();
    }

}
