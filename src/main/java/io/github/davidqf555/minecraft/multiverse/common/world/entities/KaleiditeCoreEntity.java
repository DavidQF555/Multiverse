package io.github.davidqf555.minecraft.multiverse.common.world.entities;

import io.github.davidqf555.minecraft.multiverse.common.world.RiftHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

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
        if (!level().isClientSide() && isAlive() && level().getBlockState(pos).getBlock() instanceof RiftBlock) {
            RiftHelper.destroyRift((ServerLevel) level(), pos, this);
            discard();
        }
        super.tick();
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (level() instanceof ServerLevel && isAlive()) {
            Entity owner = getOwner();
            RiftHelper.placeRandomRift((ServerLevel) level(), false, position(), owner instanceof Mob);
            discard();
        }
    }

}
