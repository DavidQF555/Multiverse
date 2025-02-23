package multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.common.util.IntRange;
import multiverse.common.world.worldgen.sea.SerializableFluidPicker;
import multiverse.common.world.worldgen.sea.WaveFluidPicker;
import multiverse.registration.custom.SeaLevelGeneratorTypeRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class WaveSeaLevelGenerator implements SeaLevelGenerator {

    public static final MapCodec<WaveSeaLevelGenerator> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            IntRange.CODEC.fieldOf("center").forGetter(sea -> sea.center),
            IntRange.CODEC.fieldOf("amplitude").forGetter(sea -> sea.amplitude),
            IntRange.CODEC.fieldOf("period").forGetter(sea -> sea.period)
    ).apply(inst, WaveSeaLevelGenerator::new));
    private final IntRange center, amplitude, period;

    public WaveSeaLevelGenerator(IntRange center, IntRange amplitude, IntRange period) {
        this.center = center;
        this.amplitude = amplitude;
        this.period = period;
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState block, RandomSource random) {
        return new WaveFluidPicker(block, center.getRandom(random), amplitude.getRandom(random), period.getRandom(random));
    }

    @Override
    public MapCodec<? extends WaveSeaLevelGenerator> getCodec() {
        return SeaLevelGeneratorTypeRegistry.WAVE.get();
    }

}
