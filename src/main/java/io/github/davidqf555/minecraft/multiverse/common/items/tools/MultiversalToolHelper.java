package io.github.davidqf555.minecraft.multiverse.common.items.tools;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.DataComponentTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MultiversalToolHelper {

    public static final Component LORE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_lore"))).withStyle(ChatFormatting.GOLD);
    public static final Component CROUCH_INSTRUCTIONS = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_crouch_instructions"))).withStyle(ChatFormatting.BLUE);
    public static final Component INSTRUCTIONS = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiversal_instructions"))).withStyle(ChatFormatting.BLUE);

    private MultiversalToolHelper() {
    }

    public static int getTarget(ItemStack stack) {
        return stack.getOrDefault(DataComponentTypeRegistry.TARGET.get(), 0);
    }

    public static boolean setTarget(ItemStack stack, int target) {
        int current = getTarget(stack);
        if (current != target) {
            stack.set(DataComponentTypeRegistry.TARGET.get(), target);
            return true;
        }
        return false;
    }

    public static void setRandomExistingTarget(ServerLevel world, ItemStack stack) {
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

    public static void setRandomTarget(Level world, ItemStack stack) {
        int current = getTarget(stack);
        int rand = world.getRandom().nextInt(ServerConfigs.INSTANCE.maxDimensions.get());
        if (rand >= current) {
            rand++;
        }
        setTarget(stack, rand);
    }

    public static boolean setCurrent(Level world, ItemStack stack) {
        return setTarget(stack, DimensionHelper.getIndex(world.dimension()));
    }

    public static void mineBlock(Player entity, ServerLevel world, ItemStack stack, BlockPos pos) {
        int target = MultiversalToolHelper.getTarget(stack);
        int current = DimensionHelper.getIndex(world.dimension());
        if (target != current) {
            DimensionHelper.getWorld(world.getServer(), target).ifPresent(w -> {
                BlockPos block = BlockPos.containing(DimensionHelper.translate(Vec3.atCenterOf(pos), world.dimensionType(), w.dimensionType(), false));
                BlockState s = w.getBlockState(block);
                if (isBreakable(w, s, block) && w.destroyBlock(block, false, entity)) {
                    PacketDistributor.sendToPlayersTrackingChunk(w, new ChunkPos(block), new RiftParticlesPacket(Optional.of(current), Vec3.atCenterOf(block)));
                    PacketDistributor.sendToPlayersTrackingChunk(world, new ChunkPos(pos), new RiftParticlesPacket(Optional.of(current), Vec3.atCenterOf(pos)));
                    Block.dropResources(s, world, pos, w.getBlockEntity(block), entity, stack);
                }
            });
        }
    }

    private static boolean isBreakable(Level world, BlockState state, BlockPos pos) {
        return !state.isAir() && state.getFluidState().isEmpty() && state.getDestroySpeed(world, pos) != -1;
    }

}
