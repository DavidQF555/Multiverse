package io.github.davidqf555.minecraft.multiverse.client.render;

import io.github.davidqf555.minecraft.multiverse.client.MultiverseColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class KaleiditeColor {

    private KaleiditeColor() {
    }

    public static class Block implements BlockColor {

        public static final Block INSTANCE = new Block();

        protected Block() {
        }

        @Override
        public int getColor(BlockState state, @Nullable BlockAndTintGetter tint, @Nullable BlockPos pos, int layer) {
            Level level = Minecraft.getInstance().level;
            return level == null ? 0xFFFFFF : MultiverseColorHelper.getColor(level);
        }

    }

    public static class Item implements ItemColor {

        public static final Item INSTANCE = new Item();

        protected Item() {
        }

        @Override
        public int getColor(ItemStack stack, int layer) {
            if (layer == 0) {
                Level level = Minecraft.getInstance().level;
                if (level != null) {
                    return MultiverseColorHelper.getColor(level);
                }
            }
            return 0xFFFFFF;
        }
    }

}
