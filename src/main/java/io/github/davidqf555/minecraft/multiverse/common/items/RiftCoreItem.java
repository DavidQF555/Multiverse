package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

import java.util.List;
import java.util.Optional;

public class RiftCoreItem extends Item {

    private static final int MAX_RANGE = 50;
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
            FeatureRegistry.RIFT.get().place(new FeaturePlaceContext<>(Optional.empty(), (ServerLevel) entity.level, ((ServerLevel) entity.level).getChunkSource().getGenerator(), entity.level.getRandom(), entity.blockPosition(), RiftConfig.of(Optional.empty(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, false), false)));
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        BlockPos pos = entity.blockPosition();
        if (entity.level.getBlockState(pos).getBlock() instanceof RiftBlock) {
            entity.level.levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, pos, 0);
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

    protected Component getLore() {
        if (lore == null) {
            lore = new TranslatableComponent(getDescriptionId() + ".lore").withStyle(ChatFormatting.LIGHT_PURPLE);
        }
        return lore;
    }

}
