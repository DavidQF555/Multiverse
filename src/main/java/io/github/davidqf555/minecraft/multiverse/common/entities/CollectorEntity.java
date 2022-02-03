package io.github.davidqf555.minecraft.multiverse.common.entities;

import io.github.davidqf555.minecraft.multiverse.common.RegistryHandler;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.SpellcastingIllagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class CollectorEntity extends SpellcastingIllagerEntity {

    private final ServerBossInfo bar;
    private UUID original;
    private int from;

    public CollectorEntity(World world) {
        super(RegistryHandler.COLLECTOR_ENTITY.get(), world);
        moveControl = new FlyingMovementController(this, 90, true);
        bar = new ServerBossInfo(getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS);
        setItemInHand(Hand.MAIN_HAND, RegistryHandler.BOUNDLESS_BLADE_ITEM.get().getDefaultInstance());
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150)
                .add(Attributes.FLYING_SPEED, 2)
                .add(Attributes.FOLLOW_RANGE, 40)
                .add(Attributes.ATTACK_DAMAGE, 5);
    }

    @Override
    protected PathNavigator createNavigation(World world) {
        FlyingPathNavigator navigator = new FlyingPathNavigator(this, world);
        navigator.setCanFloat(true);
        return navigator;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new SpellcastingIllagerEntity.CastingASpellGoal());
        goalSelector.addGoal(2, new CreateRiftGoal());
        goalSelector.addGoal(3, new FollowOriginalGoal(12, 8, 1));
        goalSelector.addGoal(4, new EnterRiftGoal(1, 16, 1));
        goalSelector.addGoal(5, new MeleeAttackGoal(this, 1, true));
        goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(8, new LookRandomlyGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new CopyOriginalGoal(new EntityPredicate().allowUnseeable().ignoreInvisibilityTesting()));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false, true));
    }

    @Override
    public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.ILLUSIONER_CAST_SPELL;
    }

    @Override
    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
        return false;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerWorld world, ITeleporter teleporter) {
        Entity entity = super.changeDimension(world, teleporter);
        if (entity instanceof CollectorEntity) {
            ((CollectorEntity) entity).setFrom(DimensionHelper.getIndex(level.dimension()));
            entity.setPortalCooldown();
        }
        return entity;
    }

    @Nullable
    public UUID getOriginalId() {
        return original;
    }

    @Nullable
    public CollectorEntity getOriginal() {
        UUID original = getOriginalId();
        if (level instanceof ServerWorld && original != null) {
            Entity entity = ((ServerWorld) level).getEntity(original);
            if (entity instanceof CollectorEntity) {
                return (CollectorEntity) entity;
            }
        }
        return null;
    }

    public void setOriginal(@Nullable UUID original) {
        this.original = original;
        bar.setVisible(this.original == null);
    }

    @Override
    public void checkDespawn() {
        if (level.getDifficulty() == Difficulty.PEACEFUL && shouldDespawnInPeaceful()) {
            remove();
        } else {
            noActionTime = 0;
        }
    }

    @Override
    protected boolean shouldDropExperience() {
        return getOriginalId() == null;
    }

    @Override
    protected boolean shouldDropLoot() {
        return getOriginalId() == null;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean ret = super.doHurtTarget(target);
        if (ret && target instanceof LivingEntity) {
            ItemStack hand = getItemInHand(getUsedItemHand());
            hand.getItem().hurtEnemy(hand, (LivingEntity) target, this);
        }
        return ret;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        boolean hurt = super.hurt(source, damage);
        if (hurt) {
            UUID original = getOriginalId();
            if (original == null) {
                Entity clone = getType().create(level);
                if (clone instanceof CollectorEntity) {
                    ((CollectorEntity) clone).setOriginal(getUUID());
                    ((LivingEntity) clone).setHealth(getHealth() / 5);
                    clone.setPos(getX(), getY(), getZ());
                    level.levelEvent(Constants.WorldEvents.ENDER_EYE_SHATTER, blockPosition(), 0);
                    level.addFreshEntity(clone);
                }
            }
        }
        return hurt;
    }

    @Override
    public ArmPose getArmPose() {
        if (isAggressive() && !isCastingSpell()) {
            return ArmPose.ATTACKING;
        }
        return super.getArmPose();
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        bar.setName(getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        bar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        bar.removePlayer(player);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bar.setPercent(getHealthRatio());
        if (getOriginalId() != null && getOriginal() == null) {
            remove();
        }
    }

    private float getHealthRatio() {
        return getHealth() / getMaxHealth();
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("From", Constants.NBT.TAG_INT)) {
            setFrom(nbt.getInt("From"));
        }
        if (nbt.contains("Original", Constants.NBT.TAG_INT_ARRAY)) {
            setOriginal(nbt.getUUID("Original"));
        }
        if (hasCustomName()) {
            bar.setName(getDisplayName());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("From", from);
        UUID original = getOriginalId();
        if (original != null) {
            nbt.putUUID("Original", original);
        }
    }

    public static class Factory implements EntityType.IFactory<CollectorEntity> {
        @Override
        public CollectorEntity create(EntityType<CollectorEntity> type, World world) {
            return new CollectorEntity(world);
        }
    }

    private class CopyOriginalGoal extends TargetGoal {

        private final EntityPredicate condition;
        private LivingEntity target;

        public CopyOriginalGoal(EntityPredicate condition) {
            super(CollectorEntity.this, false);
            this.condition = condition;
        }

        @Override
        public boolean canUse() {
            CollectorEntity original = getOriginal();
            target = original == null ? null : original.getTarget();
            return target != null && canAttack(target, condition);
        }

        @Override
        public void start() {
            setTarget(target);
            super.start();
        }
    }

    private class FollowOriginalGoal extends Goal {

        private final double start, stop;
        private final float speed;
        private int recalculate;
        private CollectorEntity original;

        private FollowOriginalGoal(double start, double stop, float speed) {
            this.speed = speed;
            this.start = start;
            this.stop = stop;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            CollectorEntity original = getOriginal();
            if (original == null || original.isSpectator() || distanceToSqr(original) < start * start) {
                return false;
            }
            this.original = original;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            if (getNavigation().isDone()) {
                return false;
            }
            return distanceToSqr(original) > stop * stop;
        }

        @Override
        public void start() {
            recalculate = 0;
        }

        @Override
        public void stop() {
            original = null;
            getNavigation().stop();
        }

        public void tick() {
            getLookControl().setLookAt(original, 10, getMaxHeadXRot());
            if (--recalculate <= 0) {
                this.recalculate = 10;
                if (!isPassenger()) {
                    if (distanceToSqr(original) >= 1024) {
                        remove();
                    } else {
                        getNavigation().moveTo(original, speed);
                    }

                }
            }
        }
    }

    private class CreateRiftGoal extends UseSpellGoal {

        private CreateRiftGoal() {
            nextAttackTickCount = tickCount + getCastingInterval();
        }

        @Override
        public boolean canUse() {
            return getOriginalId() == null && getHealthRatio() <= 0.5f && super.canUse();
        }

        @Override
        protected void performSpellCasting() {
            ItemStack hand = getItemInHand(getUsedItemHand());
            hand.releaseUsing(level, CollectorEntity.this, hand.getUseDuration() - getCastingTime());
        }

        @Override
        protected int getCastingTime() {
            return 100;
        }

        @Override
        protected int getCastingInterval() {
            return 1000;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_CAST_SPELL;
        }

        @Override
        protected SpellType getSpell() {
            return SpellType.DISAPPEAR;
        }
    }

    private class EnterRiftGoal extends MoveToBlockGoal {

        public EnterRiftGoal(double speed, int range, int verticalRange) {
            super(CollectorEntity.this, speed, range, verticalRange);
        }

        @Override
        protected boolean isValidTarget(IWorldReader world, BlockPos pos) {
            if (world.getBlockState(pos).getBlock().equals(RegistryHandler.RIFT_BLOCK.get())) {
                TileEntity tile = world.getBlockEntity(pos);
                return tile instanceof RiftTileEntity && ((RiftTileEntity) tile).getTarget() != from;
            }
            return false;
        }

        @Override
        public boolean canUse() {
            return !isOnPortalCooldown() && super.canUse();
        }
    }

}
