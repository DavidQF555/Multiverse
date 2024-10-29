package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.FlatFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelProviderTypeRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class FlatSeaLevelProvider implements SeaLevelProvider {

    public static final Codec<FlatSeaLevelProvider> CODEC = IntRange.CODEC.fieldOf("range").xmap(FlatSeaLevelProvider::new, sea -> sea.range).codec();
    private final IntRange range;

    protected FlatSeaLevelProvider(IntRange range) {
        this.range = range;
    }

    public static FlatSeaLevelProvider of(int min, int max) {
        return new FlatSeaLevelProvider(IntRange.of(min, max));
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState fluid, RandomSource random) {
        return new FlatFluidPicker(range.getRandom(random), fluid);
    }

    @Override
    public Codec<? extends FlatSeaLevelProvider> getCodec() {
        return SeaLevelProviderTypeRegistry.FLAT.get();
    }

}
