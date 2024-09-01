package io.github.davidqf555.minecraft.multiverse.common.entities;

import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.ItemRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.HitResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class KaleiditeCoreEntity extends ThrowableItemProjectile {

    private static final double MAX_RANGE = 50;

    public KaleiditeCoreEntity(EntityType<? extends KaleiditeCoreEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public KaleiditeCoreEntity(EntityType<? extends KaleiditeCoreEntity> pEntityType, LivingEntity owner, Level pLevel) {
        super(pEntityType, owner, pLevel);
    }

    public KaleiditeCoreEntity(LivingEntity owner, Level pLevel) {
        this(EntityRegistry.KALEIDITE_CORE.get(), owner, pLevel);
    }

    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.KALEIDITE_CORE.get();
    }

    @Override
    public void tick() {
        BlockPos pos = blockPosition();
        if (!level.isClientSide() && level.getBlockState(pos).getBlock() instanceof RiftBlock) {
            level.levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, pos, 0);
            removeConnected(pos, MAX_RANGE);
            discard();
        }
        super.tick();
    }

    private void removeConnected(BlockPos start, double distance) {
        int index = 0;
        List<BlockPos> list = new LinkedList<>();
        list.add(start);
        while (index < list.size()) {
            BlockPos pos = list.get(index++);
            if (pos.distSqr(start) <= distance * distance && level.getBlockState(pos).getBlock() instanceof RiftBlock) {
                BlockPos.betweenClosedStream(pos.relative(Direction.DOWN).relative(Direction.WEST).relative(Direction.SOUTH), pos.relative(Direction.UP).relative(Direction.EAST).relative(Direction.NORTH))
                        .filter(p -> !list.contains(p))
                        .map(BlockPos::immutable)
                        .forEach(list::add);
                level.destroyBlock(pos, true, this);
            }
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (level instanceof ServerLevel) {
            FeatureRegistry.RIFT.get().place(new FeaturePlaceContext<>(Optional.empty(), (ServerLevel) level, ((ServerLevel) level).getChunkSource().getGenerator(), level.getRandom(), blockPosition(), RiftConfig.of(Optional.empty(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, false), false)));
            discard();
        }
    }

}
