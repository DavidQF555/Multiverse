package io.github.davidqf555.minecraft.multiverse.common.entities;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.util.RiftHelper;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class KaleiditeCoreEntity extends ThrowableItemProjectile {

    public KaleiditeCoreEntity(EntityType<? extends KaleiditeCoreEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public KaleiditeCoreEntity(EntityType<? extends KaleiditeCoreEntity> pEntityType, LivingEntity owner, Level pLevel) {
        super(pEntityType, owner, pLevel);
    }

    public KaleiditeCoreEntity(LivingEntity owner, Level pLevel) {
        this(EntityRegistry.KALEIDITE_CORE.get(), owner, pLevel);
    }

    public KaleiditeCoreEntity(EntityType<? extends KaleiditeCoreEntity> type, double x, double y, double z, Level world) {
        super(type, x, y, z, world);
    }

    public KaleiditeCoreEntity(double x, double y, double z, Level world) {
        this(EntityRegistry.KALEIDITE_CORE.get(), x, y, z, world);
    }

    @Nonnull
    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.KALEIDITE_CORE.get();
    }

    @Override
    public void tick() {
        BlockPos pos = blockPosition();
        if (!level.isClientSide() && isAlive() && level.getBlockState(pos).getBlock() instanceof RiftBlock) {
            level.levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, pos, 0);
            removeConnected(pos, ServerConfigs.INSTANCE.coreRange.get());
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
        if (level instanceof ServerLevel && isAlive()) {
            RiftHelper.placeExplosion((ServerLevel) level, level.getRandom(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, false), Optional.empty(), Optional.empty(), position(), true);
            discard();
        }
    }

}
