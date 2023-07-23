package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;

public interface SeaLevelSelector {

    Aquifer.FluidPicker getSeaLevel(BlockState block, long seed, int index);

}
