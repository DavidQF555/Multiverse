package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class RiftCoreItem extends Item {

    private static final double MAX_RANGE = 50;
    private Component lore;

    public RiftCoreItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        lines.add(getLore());
    }

    @Override
    public void onDestroyed(ItemEntity entity, DamageSource damageSource) {
        if (!damageSource.isBypassInvul()) {
            int count = entity.getItem().getCount();
            for (int i = 0; i < count; i++) {
                FeatureRegistry.RIFT.get().place(new FeaturePlaceContext<>(Optional.empty(), (ServerLevel) entity.level, ((ServerLevel) entity.level).getChunkSource().getGenerator(), entity.level.getRandom(), entity.blockPosition(), RiftConfig.of(Optional.empty(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, false), false)));
            }
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        BlockPos pos = entity.blockPosition();
        if (entity.level.getBlockState(pos).getBlock() instanceof RiftBlock) {
            if (!entity.level.isClientSide()) {
                entity.level.levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, pos, 0);
                removeConnected(entity, pos, MAX_RANGE);
                ItemStack item = entity.getItem();
                item.shrink(1);
                entity.setItem(item);
            }
            return true;
        }
        return false;
    }

    private void removeConnected(Entity entity, BlockPos start, double distance) {
        int index = 0;
        List<BlockPos> list = new LinkedList<>();
        list.add(start);
        while (index < list.size()) {
            BlockPos pos = list.get(index++);
            if (pos.distSqr(start) <= distance * distance && entity.level.getBlockState(pos).getBlock() instanceof RiftBlock) {
                BlockPos.betweenClosedStream(pos.relative(Direction.DOWN).relative(Direction.WEST).relative(Direction.SOUTH), pos.relative(Direction.UP).relative(Direction.EAST).relative(Direction.NORTH))
                        .filter(p -> !list.contains(p))
                        .map(BlockPos::immutable)
                        .forEach(list::add);
                entity.level.destroyBlock(pos, true, entity);
            }
        }
    }

    protected Component getLore() {
        if (lore == null) {
            lore = Component.translatable(getDescriptionId() + ".lore").withStyle(ChatFormatting.LIGHT_PURPLE);
        }
        return lore;
    }

}
