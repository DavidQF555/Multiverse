package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import net.minecraft.world.level.block.state.BlockState;

public interface SeaLevelSelector {

    SerializableFluidPicker getSeaLevel(BlockState block, long seed, int index);

}
