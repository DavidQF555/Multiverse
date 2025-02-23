package multiverse.common.world.entities;

import multiverse.common.ServerConfigs;
import multiverse.common.world.RiftHelper;
import multiverse.registration.BlockRegistry;
import multiverse.registration.EntityRegistry;
import multiverse.registration.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class KaleiditeCoreEntity extends ThrowableItemProjectile {

    public KaleiditeCoreEntity(EntityType<? extends KaleiditeCoreEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public KaleiditeCoreEntity(EntityType<? extends KaleiditeCoreEntity> pEntityType, LivingEntity owner, Level pLevel, ItemStack item) {
        super(pEntityType, owner, pLevel, item);
    }

    public KaleiditeCoreEntity(LivingEntity owner, Level pLevel, ItemStack item) {
        this(EntityRegistry.KALEIDITE_CORE.get(), owner, pLevel, item);
    }

    public KaleiditeCoreEntity(EntityType<? extends KaleiditeCoreEntity> type, double x, double y, double z, Level world, ItemStack item) {
        super(type, x, y, z, world, item);
    }

    public KaleiditeCoreEntity(double x, double y, double z, Level world, ItemStack item) {
        this(EntityRegistry.KALEIDITE_CORE.get(), x, y, z, world, item);
    }

    @Nonnull
    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.KALEIDITE_CORE.get();
    }

    @Override
    public void tick() {
        BlockPos pos = blockPosition();
        if (isAlive() && level().getBlockState(pos).is(BlockRegistry.RIFT.get())) {
            RiftHelper.destroyRift(level(), pos);
            discard();
        }
        super.tick();
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (level() instanceof ServerLevel && isAlive()) {
            Entity owner = getOwner();
            RiftHelper.placeRandomRift((ServerLevel) level(), ServerConfigs.INSTANCE.coreTemporary.get(), position(), owner instanceof Mob);
            discard();
        }
    }

}
