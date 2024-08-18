package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.FlatFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelSelectorTypeRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSource;

public class FlatSeaLevelSelector extends SeaLevelSelector {

    public static final Codec<FlatSeaLevelSelector> CODEC = IntRange.CODEC.fieldOf("range").xmap(FlatSeaLevelSelector::new, sea -> sea.range).codec();
    private final IntRange range;

    protected FlatSeaLevelSelector(IntRange range) {
        this.range = range;
    }

    public static FlatSeaLevelSelector of(int min, int max) {
        return new FlatSeaLevelSelector(IntRange.of(min, max));
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState fluid, RandomSource random) {
        return new FlatFluidPicker(range.getRandom(random), fluid);
    }

    @Override
    public SeaLevelSelectorType<?> getType() {
        return SeaLevelSelectorTypeRegistry.FLAT.get();
    }

}
