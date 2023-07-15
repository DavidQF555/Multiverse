package io.github.davidqf555.minecraft.multiverse.common.entities;

import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.ItemRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CollectorEntity extends SpellcasterIllager {

    private final ServerBossEvent bar;
    private UUID original;
    private int from;

    public CollectorEntity(Level world) {
        super(EntityRegistry.COLLECTOR.get(), world);
        moveControl = new FlyingMoveControl(this, 90, true);
        bar = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
        setItemInHand(InteractionHand.MAIN_HAND, ItemRegistry.BOUNDLESS_BLADE.get().getDefaultInstance());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150)
                .add(Attributes.FLYING_SPEED, 2)
                .add(Attributes.FOLLOW_RANGE, 40)
                .add(Attributes.ATTACK_DAMAGE, 5);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation navigator = new FlyingPathNavigation(this, world);
        navigator.setCanFloat(true);
        return navigator;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new SpellcasterCastingSpellGoal());
        goalSelector.addGoal(2, new CreateRiftGoal());
        goalSelector.addGoal(3, new FollowOriginalGoal(12, 8, 1));
        goalSelector.addGoal(4, new EnterRiftGoal(1, 16, 1));
        goalSelector.addGoal(5, new MeleeAttackGoal(this, 1, true));
        goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new CopyOriginalGoal(TargetingConditions.forCombat()));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
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
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel world, ITeleporter teleporter) {
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
        if (level instanceof ServerLevel && original != null) {
            Entity entity = ((ServerLevel) level).getEntity(original);
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
            remove(RemovalReason.DISCARDED);
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
                    ((CollectorEntity) clone).from = from;
                    ((LivingEntity) clone).setHealth(getHealth() / 5);
                    clone.setPos(getX(), getY(), getZ());
                    level.levelEvent(LevelEvent.PARTICLES_EYE_OF_ENDER_DEATH, blockPosition(), 0);
                    level.addFreshEntity(clone);
                }
            }
        }
        return hurt;
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (isAggressive() && !isCastingSpell()) {
            return IllagerArmPose.ATTACKING;
        }
        return super.getArmPose();
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        bar.setName(getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bar.removePlayer(player);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bar.setProgress(getHealthRatio());
        if (getOriginalId() != null && getOriginal() == null) {
            remove(RemovalReason.DISCARDED);
        }
    }

    private float getHealthRatio() {
        return getHealth() / getMaxHealth();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("From", CompoundTag.TAG_INT)) {
            setFrom(nbt.getInt("From"));
        }
        if (nbt.contains("Original", CompoundTag.TAG_INT_ARRAY)) {
            setOriginal(nbt.getUUID("Original"));
        }
        if (hasCustomName()) {
            bar.setName(getDisplayName());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("From", from);
        UUID original = getOriginalId();
        if (original != null) {
            nbt.putUUID("Original", original);
        }
    }

    public static class Factory implements EntityType.EntityFactory<CollectorEntity> {
        @Override
        public CollectorEntity create(EntityType<CollectorEntity> type, Level world) {
            return new CollectorEntity(world);
        }
    }

    private class CopyOriginalGoal extends TargetGoal {

        private final TargetingConditions condition;
        private LivingEntity target;

        public CopyOriginalGoal(TargetingConditions condition) {
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
                        remove(RemovalReason.DISCARDED);
                    } else {
                        getNavigation().moveTo(original, speed);
                    }

                }
            }
        }
    }

    private class CreateRiftGoal extends SpellcasterUseSpellGoal {

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
        protected IllagerSpell getSpell() {
            return IllagerSpell.DISAPPEAR;
        }
    }

    private class EnterRiftGoal extends MoveToBlockGoal {

        public EnterRiftGoal(double speed, int range, int verticalRange) {
            super(CollectorEntity.this, speed, range, verticalRange);
        }

        @Override
        protected boolean isValidTarget(LevelReader world, BlockPos pos) {
            if (world.getBlockState(pos).getBlock().equals(BlockRegistry.RIFT.get())) {
                BlockEntity tile = world.getBlockEntity(pos);
                return tile instanceof RiftTileEntity && ((RiftTileEntity) tile).getTarget() != from;
            }
            return false;
        }

        @Override
        public boolean canUse() {
            return !isOnPortalCooldown() && getOriginalId() == null && super.canUse();
        }
    }

}
