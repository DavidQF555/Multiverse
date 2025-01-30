package io.github.davidqf555.minecraft.multiverse.client.colors;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class KaleiditeCurrentColor {

    private KaleiditeCurrentColor() {
    }

    public static class Block implements BlockColor {

        public static final Block INSTANCE = new Block();

        protected Block() {
        }

        @Override
        public int getColor(BlockState state, @Nullable BlockAndTintGetter tint, @Nullable BlockPos pos, int layer) {
            Level level = Minecraft.getInstance().level;
            return level == null ? 0xFFFFFFFF : MultiverseColorHelper.getColors(level.dimension(), 1)[0];
        }

    }

    public static class Item implements ItemTintSource {

        public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "kaleidite_current");
        public static final MapCodec<Item> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("default").forGetter(color -> color.defaultColor)
        ).apply(inst, Item::new));
        private final int defaultColor;

        protected Item(int defaultColor) {
            this.defaultColor = defaultColor;
        }

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) {
            if (world != null) {
                return MultiverseColorHelper.getColors(world.dimension(), 1)[0];
            }
            return defaultColor;
        }

        @Override
        public MapCodec<? extends ItemTintSource> type() {
            return CODEC;
        }

    }

}
