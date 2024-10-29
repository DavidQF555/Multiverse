package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.registration.custom.FluidPickerTypeRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;

public class WaveFluidPicker implements SerializableFluidPicker {

    public static final Codec<WaveFluidPicker> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockState.CODEC.fieldOf("state").forGetter(val -> val.state),
            Codec.INT.fieldOf("center").forGetter(val -> val.center),
            Codec.INT.fieldOf("amplitude").forGetter(val -> val.amplitude),
            Codec.INT.fieldOf("period").forGetter(val -> val.period)
    ).apply(inst, WaveFluidPicker::new));
    private final BlockState state;
    private final int center, amplitude, period;
    private final Aquifer.FluidStatus[][] states;

    public WaveFluidPicker(BlockState state, int center, int amplitude, int period) {
        this.state = state;
        this.center = center;
        this.amplitude = amplitude;
        this.period = period;
        states = new Aquifer.FluidStatus[period][period];
        for (int i = 0; i < period; i++) {
            float x = Mth.cos(Mth.TWO_PI * i / period) * amplitude / 2f;
            for (int j = 0; j < period; j++) {
                float z = Mth.cos(Mth.TWO_PI * j / period) * amplitude / 2f;
                states[i][j] = new Aquifer.FluidStatus(center + (int) (x + z + 0.5f), state);
            }
        }
    }

    @Override
    public Codec<? extends WaveFluidPicker> getCodec() {
        return FluidPickerTypeRegistry.WAVE.get();
    }

    @Override
    public Aquifer.FluidStatus computeFluid(int x, int y, int z) {
        return states[Math.floorMod(x, states.length)][Math.floorMod(z, states.length)];
    }

}
