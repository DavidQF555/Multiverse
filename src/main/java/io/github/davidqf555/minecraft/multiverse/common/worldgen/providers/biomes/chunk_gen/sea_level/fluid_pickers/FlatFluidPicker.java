package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.registration.custom.FluidPickerTypeRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;

public class FlatFluidPicker implements SerializableFluidPicker {

    public static final Codec<FlatFluidPicker> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("level").forGetter(val -> val.level),
            BlockState.CODEC.fieldOf("state").forGetter(val -> val.state)
    ).apply(inst, FlatFluidPicker::new));
    private final int level;
    private final BlockState state;
    private final Aquifer.FluidStatus fluid;

    public FlatFluidPicker(int level, BlockState state) {
        this.level = level;
        this.state = state;
        this.fluid = new Aquifer.FluidStatus(level, state);
    }

    @Override
    public Codec<? extends FlatFluidPicker> getCodec() {
        return FluidPickerTypeRegistry.FLAT.get();
    }

    @Override
    public Aquifer.FluidStatus computeFluid(int i, int i1, int i2) {
        return fluid;
    }

}
