package io.github.davidqf555.minecraft.multiverse.common.entities;

import io.github.davidqf555.minecraft.multiverse.client.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.entities.ai.FollowEntityGoal;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TravelerEntity extends AbstractIllager implements CrossbowAttackMob {

    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(TravelerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final byte RIFT_PARTICLES_EVENT = 50;
    private static final int MAX_DOPPELGANGERS = 10, SPAWN_PERIOD = 20, PARTICLES_COUNT = 50;
    private static final float CROSSBOW_POWER = 1.6f;
    private static final double MIN_TP = 8, MAX_TP = 16;
    private final ServerBossEvent bar;
    private UUID original;

    public TravelerEntity(EntityType<? extends TravelerEntity> type, Level world) {
        super(type, world);
        moveControl = new FlyingMoveControl(this, 90, true);
        bar = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150)
                .add(Attributes.FLYING_SPEED, 2)
                .add(Attributes.FOLLOW_RANGE, 40)
                .add(Attributes.ATTACK_DAMAGE, 5);
    }

    public static boolean canSpawn(EntityType<? extends TravelerEntity> type, ServerLevelAccessor level, MobSpawnType spawn, BlockPos pos, RandomSource rand) {
        return spawn != MobSpawnType.NATURAL && (spawn != MobSpawnType.CHUNK_GENERATION || DimensionHelper.getIndex(level.getLevel().dimension()) != 0 && rand.nextDouble() < ServerConfigs.INSTANCE.travelerSpawnFactor.get());
    }

    private void doRiftEffect() {
        level().broadcastEntityEvent(this, RIFT_PARTICLES_EVENT);
    }

    @Override
    protected void tickDeath() {
        if (level().isClientSide() || getOriginalId() == null) {
            super.tickDeath();
        } else {
            doRiftEffect();
            discard();
        }
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == RIFT_PARTICLES_EVENT) {
            int color = MultiverseColorHelper.getColor(level(), random.nextInt(ServerConfigs.INSTANCE.maxDimensions.get() + 1));
            for (int i = 0; i < PARTICLES_COUNT; i++) {
                level().addParticle(ParticleTypeRegistry.RIFT.get(), getRandomX(1), getRandomY(), getRandomZ(1), FastColor.ARGB32.red(color) / 255.0, FastColor.ARGB32.green(color) / 255.0, FastColor.ARGB32.blue(color) / 255.0);
            }
        }
        super.handleEntityEvent(b);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(IS_CHARGING_CROSSBOW, false);
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
        goalSelector.addGoal(1, new RangedCrossbowAttackGoal<>(this, 1, 16));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, true));
        goalSelector.addGoal(3, new FollowEntityGoal<>(this, TravelerEntity::getOriginal, 12, 8, 1));
        goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new CopyOriginalGoal(TargetingConditions.forCombat()));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
    }

    @Nullable
    public UUID getOriginalId() {
        return original;
    }

    @Nullable
    public TravelerEntity getOriginal() {
        UUID original = getOriginalId();
        if (level() instanceof ServerLevel && original != null) {
            Entity entity = ((ServerLevel) level()).getEntity(original);
            if (entity instanceof TravelerEntity) {
                return (TravelerEntity) entity;
            }
        }
        return null;
    }

    public void setOriginal(@Nullable UUID original) {
        this.original = original;
        bar.setVisible(this.original == null);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return getOriginalId() == null;
    }

    @Override
    public boolean shouldDropExperience() {
        return getOriginalId() == null;
    }

    @Override
    protected boolean shouldDropLoot() {
        return getOriginalId() == null;
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (super.hurt(source, damage)) {
            EntityUtil.randomTeleport(this, position(), MIN_TP, MAX_TP, true);
            return true;
        }
        return false;
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

    public List<TravelerEntity> getDoppelgangers() {
        List<TravelerEntity> doppelgangers = new ArrayList<>();
        if (getOriginalId() == null && !level().isClientSide()) {
            UUID id = getUUID();
            for (Entity entity : ((ServerLevel) level()).getAllEntities()) {
                if (entity instanceof TravelerEntity && id.equals(((TravelerEntity) entity).getOriginalId())) {
                    doppelgangers.add((TravelerEntity) entity);
                }
            }
        }
        return doppelgangers;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bar.setProgress(getHealthRatio());
        if (getOriginalId() == null) {
            LivingEntity target = getTarget();
            if (level().getGameTime() % SPAWN_PERIOD == 0 && target != null && getDoppelgangers().size() < MAX_DOPPELGANGERS) {
                Entity clone = getType().spawn((ServerLevel) level(), target.blockPosition(), MobSpawnType.MOB_SUMMONED);
                if (clone instanceof TravelerEntity) {
                    EntityUtil.randomTeleport((LivingEntity) clone, target.position(), MIN_TP, MAX_TP, false);
                    ((TravelerEntity) clone).setOriginal(getUUID());
                    ((LivingEntity) clone).setHealth(getHealth() / 5);
                    ((TravelerEntity) clone).doRiftEffect();
                }
            }
        } else if (getOriginal() == null) {
            kill();
        }
    }

    private float getHealthRatio() {
        return getHealth() / getMaxHealth();
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem item) {
        return item instanceof CrossbowItem;
    }

    public boolean isChargingCrossbow() {
        return getEntityData().get(IS_CHARGING_CROSSBOW);
    }

    public void setChargingCrossbow(boolean charging) {
        getEntityData().set(IS_CHARGING_CROSSBOW, charging);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        noActionTime = 0;
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (isChargingCrossbow()) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
        } else if (isHolding(is -> is.getItem() instanceof CrossbowItem)) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
        } else if (isAggressive()) {
            return AbstractIllager.IllagerArmPose.ATTACKING;
        }
        return AbstractIllager.IllagerArmPose.NEUTRAL;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader reader) {
        return 0;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        populateDefaultEquipmentSlots(random, difficulty);
        populateDefaultEquipmentEnchantments(random, difficulty);
        return super.finalizeSpawn(level, difficulty, type, data, tag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        setItemInHand(InteractionHand.MAIN_HAND, (random.nextBoolean() ? Items.IRON_AXE : Items.CROSSBOW).getDefaultInstance());
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float dist) {
        performCrossbowAttack(this, CROSSBOW_POWER);
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity entity, ItemStack stack, Projectile projectile, float dist) {
        shootCrossbowProjectile(this, entity, projectile, dist, CROSSBOW_POWER);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) {
            return true;
        } else if (entity instanceof LivingEntity && ((LivingEntity) entity).getMobType() == MobType.ILLAGER) {
            return getTeam() == null && entity.getTeam() == null;
        } else {
            return false;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33306_) {
        return SoundEvents.PILLAGER_HURT;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Original", CompoundTag.TAG_INT_ARRAY)) {
            setOriginal(tag.getUUID("Original"));
        }
        if (hasCustomName()) {
            bar.setName(getDisplayName());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        UUID original = getOriginalId();
        if (original != null) {
            nbt.putUUID("Original", original);
        }
    }

    private class CopyOriginalGoal extends TargetGoal {

        private final TargetingConditions condition;
        private LivingEntity target;

        public CopyOriginalGoal(TargetingConditions condition) {
            super(TravelerEntity.this, false);
            this.condition = condition;
        }

        @Override
        public boolean canUse() {
            TravelerEntity original = getOriginal();
            target = original == null ? null : original.getTarget();
            return target != null && canAttack(target, condition);
        }

        @Override
        public void start() {
            setTarget(target);
            super.start();
        }
    }

}
