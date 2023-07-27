package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RiftRemovalItem extends Item {

    private static final int MAX_RANGE = 50;

    public RiftRemovalItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        BlockPos pos = entity.blockPosition();
        if (entity.level.getBlockState(pos).getBlock() instanceof RiftBlock) {
            removeConnected(entity, pos, 0);
            entity.discard();
            return true;
        }
        return false;
    }

    private void removeConnected(Entity entity, BlockPos pos, int distance) {
        if (distance <= MAX_RANGE && entity.level.getBlockState(pos).getBlock() instanceof RiftBlock) {
            entity.level.destroyBlock(pos, true, entity);
            for (BlockPos surrounding : BlockPos.betweenClosed(pos.relative(Direction.DOWN).relative(Direction.EAST).relative(Direction.NORTH), pos.relative(Direction.UP).relative(Direction.WEST).relative(Direction.SOUTH))) {
                if (!surrounding.equals(pos)) {
                    removeConnected(entity, surrounding, distance + 1);
                }
            }
        }
    }

}
