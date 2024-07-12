package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;

public class FlatFluidPicker implements SerializableFluidPicker {

    public static final MapCodec<FlatFluidPicker> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("level").forGetter(picker -> picker.level),
            BlockState.CODEC.fieldOf("fluid").forGetter(picker -> picker.state)
    ).apply(inst, FlatFluidPicker::new));
    private final Aquifer.FluidStatus fluid;
    private final int level;
    private final BlockState state;

    public FlatFluidPicker(int level, BlockState fluid) {
        this.level = level;
        state = fluid;
        this.fluid = new Aquifer.FluidStatus(level, fluid);
    }

    @Override
    public Aquifer.FluidStatus computeFluid(int x, int y, int z) {
        return fluid;
    }

    @Override
    public MapCodec<? extends FlatFluidPicker> codec() {
        return CODEC;
    }

}