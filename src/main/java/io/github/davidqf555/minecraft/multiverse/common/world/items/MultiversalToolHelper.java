package io.github.davidqf555.minecraft.multiverse.common.world.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftEffectPacket;
import io.github.davidqf555.minecraft.multiverse.common.world.RiftHelper;
import io.github.davidqf555.minecraft.multiverse.registration.DataComponentTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public final class MultiversalToolHelper {

    public static final Component LORE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_lore"))).withStyle(ChatFormatting.GOLD);
    public static final Component SELECT_CURRENT = Component.literal(" ").append(Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_select_current"))).withStyle(ChatFormatting.AQUA));
    public static final Component SELECT_RANDOM = Component.literal(" ").append(Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_select_random"))).withStyle(ChatFormatting.AQUA));

    private MultiversalToolHelper() {
    }

    public static Component getShiftRightHeader() {
        return Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_header")), Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_header.plus")), Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_header.hold")), Component.keybind("key.sneak")), Component.keybind("key.mouse.right"))).withStyle(ChatFormatting.BLUE);
    }

    public static Component getRightHeader() {
        return Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_header")), Component.keybind("key.mouse.right")).withStyle(ChatFormatting.BLUE);
    }

    public static Component getHoldRightHeader() {
        return Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_header")), Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_header.hold")), Component.keybind("key.mouse.right"))).withStyle(ChatFormatting.BLUE);
    }

    public static ResourceKey<Level> getTarget(ItemStack stack) {
        return stack.getOrDefault(DataComponentTypeRegistry.TARGET.get(), Level.OVERWORLD);
    }

    public static boolean setTarget(ItemStack stack, ResourceKey<Level> target) {
        if (!getTarget(stack).equals(target)) {
            stack.set(DataComponentTypeRegistry.TARGET.get(), target);
            return true;
        }
        return false;
    }

    public static void setRandomTarget(Level world, ItemStack stack) {
        ResourceKey<Level> current = getTarget(stack);
        RiftHelper.randomTargetDimension(world.getRandom(), current).ifPresent(target -> setTarget(stack, target));
    }

    public static boolean setCurrent(Level world, ItemStack stack) {
        return setTarget(stack, world.dimension());
    }

    public static void mineBlock(Player entity, ServerLevel world, ItemStack stack, BlockPos pos) {
        ResourceKey<Level> target = MultiversalToolHelper.getTarget(stack);
        ResourceKey<Level> current = world.dimension();
        if (target != current) {
            ServerLevel w = world.getServer().getLevel(target);
            if (w != null) {
                BlockPos block = BlockPos.containing(RiftHelper.translate(Vec3.atCenterOf(pos), world.dimensionType(), w.dimensionType(), false));
                BlockState s = w.getBlockState(block);
                if (isBreakable(w, s, block) && w.destroyBlock(block, false, entity)) {
                    PacketDistributor.sendToPlayersTrackingChunk(w, new ChunkPos(block), new RiftEffectPacket(Vec3.atCenterOf(block), SoundSource.BLOCKS, current));
                    PacketDistributor.sendToPlayersTrackingChunk(world, new ChunkPos(pos), new RiftEffectPacket(Vec3.atCenterOf(pos), SoundSource.BLOCKS, target));
                    Block.dropResources(s, world, pos, w.getBlockEntity(block), entity, stack);
                }
            }
        }
    }

    private static boolean isBreakable(Level world, BlockState state, BlockPos pos) {
        return !state.isAir() && state.getFluidState().isEmpty() && state.getDestroySpeed(world, pos) != -1;
    }

}
