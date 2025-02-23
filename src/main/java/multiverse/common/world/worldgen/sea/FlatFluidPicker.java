package multiverse.common.world.worldgen.sea;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.FluidPickerType;
import multiverse.registration.custom.FluidPickerTypeRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;

import javax.annotation.Nonnull;

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
    public FluidPickerType<? extends FlatFluidPicker> getType() {
        return FluidPickerTypeRegistry.FLAT.get();
    }

    @Nonnull
    @Override
    public Aquifer.FluidStatus computeFluid(int i, int i1, int i2) {
        return fluid;
    }

}
