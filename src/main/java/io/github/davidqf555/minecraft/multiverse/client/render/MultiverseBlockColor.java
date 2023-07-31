package io.github.davidqf555.minecraft.multiverse.client.render;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MultiverseBlockColor implements BlockColor {

    public static final MultiverseBlockColor INSTANCE = new MultiverseBlockColor();

    protected MultiverseBlockColor() {
    }

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter tint, @Nullable BlockPos pos, int layer) {
        Level level = Minecraft.getInstance().level;
        return level == null ? 0xFFFFFF : MultiverseColorHelper.getColor(level, DimensionHelper.getIndex(level.dimension()));
    }

}
