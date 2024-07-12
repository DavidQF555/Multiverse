package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;

public class WaveFluidPicker implements SerializableFluidPicker {

    public static final MapCodec<WaveFluidPicker> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockState.CODEC.fieldOf("fluid").forGetter(picker -> picker.fluid),
            Codec.INT.fieldOf("center").forGetter(picker -> picker.center),
            Codec.INT.fieldOf("amplitude").forGetter(picker -> picker.amplitude),
            Codec.INT.fieldOf("period").forGetter(picker -> picker.period)
    ).apply(inst, WaveFluidPicker::new));
    private final Aquifer.FluidStatus[][] states;
    private final BlockState fluid;
    private final int center, amplitude, period;

    public WaveFluidPicker(BlockState fluid, int center, int amplitude, int period) {
        this.fluid = fluid;
        this.center = center;
        this.amplitude = amplitude;
        this.period = period;
        states = new Aquifer.FluidStatus[period][period];
        for (int i = 0; i < period; i++) {
            float x = Mth.cos(Mth.TWO_PI * i / period) * amplitude / 2f;
            for (int j = 0; j < period; j++) {
                float z = Mth.cos(Mth.TWO_PI * j / period) * amplitude / 2f;
                states[i][j] = new Aquifer.FluidStatus(center + (int) (x + z + 0.5f), fluid);
            }
        }
    }

    @Override
    public Aquifer.FluidStatus computeFluid(int x, int y, int z) {
        return states[Math.floorMod(x, states.length)][Math.floorMod(z, states.length)];
    }

    @Override
    public MapCodec<? extends WaveFluidPicker> codec() {
        return CODEC;
    }

}

