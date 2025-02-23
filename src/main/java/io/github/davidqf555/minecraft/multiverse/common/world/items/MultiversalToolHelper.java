package io.github.davidqf555.minecraft.multiverse.common.world.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftEffectPacket;
import io.github.davidqf555.minecraft.multiverse.common.world.RiftHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public final class MultiversalToolHelper {

    public static final Component LORE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "multiversal_lore"))).withStyle(ChatFormatting.GOLD);
    public static final Component SELECT_CURRENT = Component.literal(" ").append(Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "multiversal_select_current"))).withStyle(ChatFormatting.AQUA));
    public static final Component SELECT_RANDOM = Component.literal(" ").append(Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "multiversal_select_random"))).withStyle(ChatFormatting.AQUA));

    private MultiversalToolHelper() {
    }

    public static Component getShiftRightHeader() {
        return Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "multiversal_header")), Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "multiversal_header.plus")), Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "multiversal_header.hold")), Component.keybind("key.sneak")), Component.keybind("key.mouse.right"))).withStyle(ChatFormatting.BLUE);
    }

    public static Component getRightHeader() {
        return Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "multiversal_header")), Component.keybind("key.mouse.right")).withStyle(ChatFormatting.BLUE);
    }

    public static Component getHoldRightHeader() {
        return Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "multiversal_header")), Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "multiversal_header.hold")), Component.keybind("key.mouse.right"))).withStyle(ChatFormatting.BLUE);
    }

    public static ResourceKey<Level> getTarget(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement(Multiverse.MOD_ID);
        return tag.contains("Target", Tag.TAG_STRING) ? ResourceKey.create(Registries.DIMENSION, new ResourceLocation(tag.getString("Target"))) : Level.OVERWORLD;
    }

    public static boolean setTarget(ItemStack stack, ResourceKey<Level> target) {
        if (!getTarget(stack).equals(target)) {
            CompoundTag tag = stack.getOrCreateTagElement(Multiverse.MOD_ID);
            tag.putString("Target", target.location().toString());
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
                    Multiverse.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> w.getChunkAt(block)), new RiftEffectPacket(Vec3.atCenterOf(block), SoundSource.BLOCKS, current));
                    Multiverse.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new RiftEffectPacket(Vec3.atCenterOf(pos), SoundSource.BLOCKS, target));
                    Block.dropResources(s, new LootContext.Builder(world)
                            .withRandom(entity.getRandom())
                            .withParameter(LootContextParams.TOOL, stack)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                            .withParameter(LootContextParams.BLOCK_STATE, s)
                            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, w.getBlockEntity(block))
                            .withParameter(LootContextParams.THIS_ENTITY, entity)
                    );
                }
            }
        }
    }

    private static boolean isBreakable(Level world, BlockState state, BlockPos pos) {
        return !state.isAir() && state.getFluidState().isEmpty() && state.getDestroySpeed(world, pos) != -1;
    }

}
