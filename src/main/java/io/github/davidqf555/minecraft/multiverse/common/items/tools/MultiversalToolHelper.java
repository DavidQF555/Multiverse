package io.github.davidqf555.minecraft.multiverse.common.items.tools;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public final class MultiversalToolHelper {

    public static final Component LORE = new TranslatableComponent(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "multiversal_lore"))).withStyle(ChatFormatting.LIGHT_PURPLE);
    public static final Component INSTRUCTIONS = new TranslatableComponent(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "multiversal_instructions"))).withStyle(ChatFormatting.BLUE);
    private static final int PARTICLES = 50;

    private MultiversalToolHelper() {
    }

    public static int getTarget(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement(Multiverse.MOD_ID);
        return tag.contains("Target", Tag.TAG_INT) ? tag.getInt("Target") : 0;
    }

    public static void setTarget(ItemStack stack, int target) {
        CompoundTag tag = stack.getOrCreateTagElement(Multiverse.MOD_ID);
        tag.putInt("Target", target);
    }

    public static void setRandomTarget(ServerLevel world, ItemStack stack) {
        int current = getTarget(stack);
        List<Integer> existing = new ArrayList<>();
        int max = ServerConfigs.INSTANCE.maxDimensions.get();
        for (int i = 0; i <= max; i++) {
            if (DimensionHelper.getWorld(world.getServer(), i).isPresent()) {
                existing.add(i);
            }
        }
        existing.remove(Integer.valueOf(current));
        setTarget(stack, existing.isEmpty() ? 0 : existing.get(world.getRandom().nextInt(existing.size())));
    }

    public static void setCurrent(Level world, ItemStack stack) {
        setTarget(stack, DimensionHelper.getIndex(world.dimension()));
    }

    public static void mineBlock(Entity entity, ServerLevel world, ItemStack stack, BlockPos pos) {
        int target = MultiversalToolHelper.getTarget(stack);
        int current = DimensionHelper.getIndex(world.dimension());
        if (target != current) {
            DimensionHelper.getWorld(world.getServer(), target).ifPresent(w -> {
                double scale = DimensionType.getTeleportationScale(w.dimensionType(), world.dimensionType());
                BlockPos block = new BlockPos(pos.getX() * scale, pos.getY(), pos.getZ() * scale);
                BlockState s = w.getBlockState(block);
                if (isBreakable(w, s, block) && w.destroyBlock(block, false, entity)) {
                    Multiverse.CHANNEL.send(PacketDistributor.DIMENSION.with(w::dimension), new RiftParticlesPacket(OptionalInt.of(current), Vec3.atCenterOf(block), 0.5, PARTICLES));
                    Block.dropResources(s, world, pos);
                }
            });
        }
    }

    private static boolean isBreakable(Level world, BlockState state, BlockPos pos) {
        return !state.isAir() && state.getFluidState().isEmpty() && state.getDestroySpeed(world, pos) != -1;
    }

}
