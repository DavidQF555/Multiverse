package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.WaveFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelSelectorTypeRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSource;

public class WaveSeaLevelSelector extends SeaLevelSelector {

    public static final Codec<WaveSeaLevelSelector> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            IntRange.CODEC.fieldOf("center").forGetter(sea -> sea.center),
            IntRange.CODEC.fieldOf("amplitude").forGetter(sea -> sea.amplitude),
            IntRange.CODEC.fieldOf("period").forGetter(sea -> sea.period)
    ).apply(inst, WaveSeaLevelSelector::new));
    private final IntRange center, amplitude, period;

    public WaveSeaLevelSelector(IntRange center, IntRange amplitude, IntRange period) {
        this.center = center;
        this.amplitude = amplitude;
        this.period = period;
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState block, RandomSource random) {
        return new WaveFluidPicker(block, center.getRandom(random), amplitude.getRandom(random), period.getRandom(random));
    }

    @Override
    public SeaLevelSelectorType<?> getType() {
        return SeaLevelSelectorTypeRegistry.WAVE.get();
    }

}
