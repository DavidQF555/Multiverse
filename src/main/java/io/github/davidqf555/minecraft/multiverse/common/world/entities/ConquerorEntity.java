package io.github.davidqf555.minecraft.multiverse.common.world.entities;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.PublicRaidType;
import io.github.davidqf555.minecraft.multiverse.common.world.RiftHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.ai.FlyingMoveThroughVillageGoal;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.ai.FlyingPathfindToRaidGoal;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.ai.NoGravityNavigator;
import io.github.davidqf555.minecraft.multiverse.registration.AttachmentTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ConquerorEntity extends SpellcasterIllager {

    private final ServerBossEvent bar;

    public ConquerorEntity(EntityType<? extends ConquerorEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        bar = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
        moveControl = new FlyingMoveControl(this, 20, true);
        setPathfindingMalus(PathType.LAVA, 8);
        setPathfindingMalus(PathType.DANGER_FIRE, 0);
        setPathfindingMalus(PathType.DAMAGE_FIRE, 0);
        setItemInHand(InteractionHand.MAIN_HAND, ItemRegistry.PRISMATIC_AXE.get().getDefaultInstance());
        xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200)
                .add(Attributes.FLYING_SPEED, 0.2)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ARMOR, 11)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ATTACK_DAMAGE, 1)
                .add(Attributes.ATTACK_KNOCKBACK, 5)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.GRAVITY, 0);
    }

    @Override
    protected float getFlyingSpeed() {
        return getSpeed();
    }

    @Override
    public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
        return false;
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (isCastingSpell()) {
            return IllagerArmPose.SPELLCASTING;
        } else if (isAggressive()) {
            return IllagerArmPose.ATTACKING;
        } else {
            return isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.NEUTRAL;
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bar.setProgress(getHealth() / getMaxHealth());
    }

    @Override
    public void setCustomName(@javax.annotation.Nullable Component name) {
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
    protected PathNavigation createNavigation(Level pLevel) {
        NoGravityNavigator navigator = new NoGravityNavigator(this, pLevel);
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

        goalSelector.addGoal(0, new SpellcasterCastingSpellGoal());
        goalSelector.addGoal(1, new SpawnRiftGoal());
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 2, false));
        goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 3, 1));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Mob.class, 8));
        targetSelector.addGoal(0, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return super.isInvulnerableTo(pSource) || pSource.is(DamageTypeTags.IS_FIRE) || pSource.is(DamageTypeTags.IS_DROWNING);
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    public void applyRaidBuffs(ServerLevel level, int wave, boolean unused) {
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (hasCustomName()) {
            bar.setName(getDisplayName());
        }
    }

    public class SpawnRiftGoal extends SpellcasterUseSpellGoal {

        @Override
        public boolean canUse() {
            LivingEntity target = getTarget();
            if (!super.canUse()) {
                return false;
            } else if (target == null) {
                if (!hasActiveRaid()) {
                    return false;
                }
                return getCurrentRaid().getTotalRaidersAlive() < ServerConfigs.INSTANCE.conquerorMobThreshold.get();
            } else if (target.distanceToSqr(position()) > ServerConfigs.INSTANCE.conquerorDistanceThreshold.get() * ServerConfigs.INSTANCE.conquerorDistanceThreshold.get()) {
                double range = getAttributeValue(Attributes.FOLLOW_RANGE);
                AABB bounds = AABB.ofSize(getEyePosition(), range * 2, range * 2, range * 2);
                return level().getEntitiesOfClass(Raider.class, bounds).size() < ServerConfigs.INSTANCE.conquerorMobThreshold.get();
            }
            return false;
        }

        @Override
        protected void performSpellCasting() {
            Map<EntityType<?>, Integer> entities = new HashMap<>();
            for (int i = 0; i < ServerConfigs.INSTANCE.conquerorSpawnCount.get(); i++) {
                entities.compute(selectEntityType(), (k, v) -> v == null ? 1 : v + 1);
            }
            Vec3 pos = getRiftTarget(entities.keySet());
            RiftHelper.placeRandomRift((ServerLevel) level(), false, pos, true);
            BlockPos block = BlockPos.containing(pos);
            entities.forEach((type, n) -> {
                Raider entity = (Raider) type.spawn((ServerLevel) level(), block, MobSpawnType.REINFORCEMENT);
                if (entity != null) {
                    Raid raid = getCurrentRaid();
                    if (Raids.canJoinRaid(entity, raid)) {
                        raid.joinRaid(raid.getGroupsSpawned(), entity, null, true);
                    }
                    entity.setPortalCooldown();

                    if (ServerConfigs.INSTANCE.conquerorSlowFallingAmplifier.get() > 0 && ServerConfigs.INSTANCE.conquerorSlowFallingDuration.get() > 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, ServerConfigs.INSTANCE.conquerorSlowFallingDuration.get(), ServerConfigs.INSTANCE.conquerorSlowFallingAmplifier.get() - 1));
                    }
                    entity.setTarget(getTarget());
                    entity.setData(AttachmentTypeRegistry.SUMMONED, true);
                }
            });
        }

        @Nullable
        protected EntityType<? extends Raider> selectEntityType() {
            List<Pair<Raid.RaiderType, Integer>> weights = new ArrayList<>();
            int total = 0;
            for (Raid.RaiderType type : Raid.RaiderType.values()) {
                int amt = Arrays.stream(type.spawnsPerWaveBeforeBonus).reduce(0, Integer::sum);
                weights.add(Pair.of(type, amt));
                total += amt;
            }
            if (total <= 0) {
                return null;
            }
            int rand = getRandom().nextInt(total);
            for (Pair<Raid.RaiderType, Integer> pair : weights) {
                total -= pair.getSecond();
                if (rand >= total) {
                    return ((PublicRaidType) (Object) pair.getFirst()).getEntityType();
                }
            }
            throw new RuntimeException("should not ever get here");
        }

        protected Vec3 getRiftTarget(Set<EntityType<?>> types) {
            Vec3 center;
            LivingEntity target = getTarget();
            if (target != null) {
                center = target.getEyePosition();
            } else if (hasActiveRaid()) {
                center = Vec3.atCenterOf(getCurrentRaid().getCenter());
            } else {
                center = getEyePosition();
            }
            return EntityHelper.getRandomSpawnAbove((ServerLevel) level(), getRandom(), center, ServerConfigs.INSTANCE.conquerorMaxSpawnHDist.get(), ServerConfigs.INSTANCE.conquerorMinSpawnDist.get(), ServerConfigs.INSTANCE.conquerorMaxSpawnDist.get(), types);
        }

        @Override
        protected int getCastingTime() {
            return ServerConfigs.INSTANCE.conquerorCastTime.get();
        }

        @Override
        protected int getCastingInterval() {
            return ServerConfigs.INSTANCE.conquerorCooldown.get();
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.DISAPPEAR;
        }

    }

}
