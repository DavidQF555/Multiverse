package multiverse.common.world.entities;

import multiverse.common.Multiverse;
import multiverse.common.ServerConfigs;
import multiverse.common.packets.RiftEffectPacket;
import multiverse.common.world.capabilities.SummonedData;
import multiverse.common.world.entities.ai.FlyingMoveThroughVillageGoal;
import multiverse.common.world.entities.ai.FlyingPathfindToRaidGoal;
import multiverse.common.world.entities.ai.FollowEntityGoal;
import multiverse.common.world.entities.ai.NoGravityNavigator;
import multiverse.registration.EffectRegistry;
import multiverse.registration.ItemRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
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
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TravelerEntity extends AbstractIllager implements CrossbowAttackMob {

    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(TravelerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final float CROSSBOW_POWER = 1.6f;
    private final ServerBossEvent bar;
    private UUID original;

    public TravelerEntity(EntityType<? extends TravelerEntity> type, Level world) {
        super(type, world);
        moveControl = new FlyingMoveControl(this, 20, true);
        bar = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
        setPathfindingMalus(BlockPathTypes.LAVA, 8);
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0);
        setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0);
        xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150)
                .add(Attributes.FLYING_SPEED, 0.2)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(ForgeMod.ENTITY_GRAVITY.get(), 0);
    }

    @Override
    public void setSpeed(float pSpeed) {
        super.setSpeed(pSpeed);
        flyingSpeed = pSpeed;
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
        if (!level.isClientSide() && getOriginalId() == null) {
            Entity entity = pCause.getEntity();
            Player player = null;
            if (entity instanceof Player) {
                player = (Player) entity;
            } else if (entity instanceof TamableAnimal) {
                LivingEntity owner = ((TamableAnimal) entity).getOwner();
                if (((TamableAnimal) entity).isTame() && owner instanceof Player) {
                    player = (Player) owner;
                }
            }
            if (player != null && !level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS) && player.getRandom().nextDouble() < ServerConfigs.INSTANCE.bountyRate.get()) {
                MobEffectInstance effect = new MobEffectInstance(EffectRegistry.BOUNTY.get(), 120000, 0, false, false, true);
                player.addEffect(effect);
            }
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return super.isInvulnerableTo(pSource) || pSource.isFire() || pSource == DamageSource.DROWN;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(IS_CHARGING_CROSSBOW, false);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        NoGravityNavigator navigator = new NoGravityNavigator(this, world);
        navigator.setCanFloat(true);
        navigator.setCanPassDoors(true);
        return navigator;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(4, new LongDistancePatrolGoal<>(this, 0.7, 0.595));
        goalSelector.addGoal(3, new ObtainRaidLeaderBannerGoal<>(this));
        goalSelector.addGoal(3, new FlyingPathfindToRaidGoal(this, 1));
        goalSelector.addGoal(4, new FlyingMoveThroughVillageGoal(this, 1.05, 1));
        goalSelector.addGoal(5, new RaiderCelebration(this));
        goalSelector.addGoal(0, new RangedCrossbowAttackGoal<>(this, 1, 16));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
        goalSelector.addGoal(2, new FollowEntityGoal<>(this, TravelerEntity::getOriginal, 12, 8, 1));
        goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        targetSelector.addGoal(1, new CopyOriginalGoal(TargetingConditions.forCombat()));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Nullable
    public UUID getOriginalId() {
        return original;
    }

    @Nullable
    public TravelerEntity getOriginal() {
        UUID original = getOriginalId();
        if (level instanceof ServerLevel && original != null) {
            Entity entity = ((ServerLevel) level).getEntity(original);
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
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (super.hurt(source, damage) && getOriginalId() == null) {
            EntityHelper.randomTeleport(this, position(), ServerConfigs.INSTANCE.travelerMinRange.get(), ServerConfigs.INSTANCE.travelerMaxRange.get(), true);
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
        if (getOriginalId() == null && !level.isClientSide()) {
            UUID id = getUUID();
            for (Entity entity : ((ServerLevel) level).getAllEntities()) {
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
        bar.setProgress(getHealth() / getMaxHealth());
        if (getOriginalId() == null) {
            LivingEntity target = getTarget();
            if (level.getGameTime() % ServerConfigs.INSTANCE.travelerDoppelPeriod.get() == 0 && target != null && getDoppelgangers().size() < ServerConfigs.INSTANCE.travelerMaxDoppel.get()) {
                Entity clone = EntityHelper.randomSpawn(getType(), (ServerLevel) level, target.blockPosition(), ServerConfigs.INSTANCE.travelerMinRange.get(), ServerConfigs.INSTANCE.travelerMaxRange.get(), MobSpawnType.REINFORCEMENT);
                if (clone instanceof TravelerEntity) {
                    SummonedData.setSummoned((Mob) clone, true);
                    ((TravelerEntity) clone).setOriginal(getUUID());
                    ((LivingEntity) clone).setHealth(getHealth() / 5);
                    Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> clone), new RiftEffectPacket(clone.getEyePosition(), clone.getSoundSource(), null));
                }
            }
        } else if (getOriginal() == null) {
            kill();
        }
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

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        populateDefaultEquipmentSlots(difficulty);
        populateDefaultEquipmentEnchantments(difficulty);
        return super.finalizeSpawn(level, difficulty, type, data, tag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(difficulty);
        setItemInHand(InteractionHand.MAIN_HAND, (random.nextBoolean() ? ItemRegistry.KALEIDITE_AXE.get() : Items.CROSSBOW).getDefaultInstance());
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
