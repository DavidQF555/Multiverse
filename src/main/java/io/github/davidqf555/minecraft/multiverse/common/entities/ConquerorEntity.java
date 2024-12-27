package io.github.davidqf555.minecraft.multiverse.common.entities;

import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import io.github.davidqf555.minecraft.multiverse.common.util.EntityUtil;
import io.github.davidqf555.minecraft.multiverse.common.util.RiftCoordinationHelper;
import io.github.davidqf555.minecraft.multiverse.registration.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.Nullable;

public class ConquerorEntity extends SpellcasterIllager {

    public ConquerorEntity(EntityType<? extends ConquerorEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        moveControl = new FlyingMoveControl(this, 90, true);
        setNoGravity(true);
        setItemInHand(InteractionHand.MAIN_HAND, ItemRegistry.KALEIDITE_AXE.get().getDefaultInstance());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150)
                .add(Attributes.FLYING_SPEED, 3)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.ATTACK_KNOCKBACK, 10)
                .add(ForgeMod.ENTITY_GRAVITY.get(), 0);
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (isCastingSpell()) {
            return IllagerArmPose.SPELLCASTING;
        } else if (isAggressive()) {
            return IllagerArmPose.ATTACKING;
        } else {
            return isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.CROSSED;
        }
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation navigator = new FlyingPathNavigation(this, pLevel);
        navigator.setCanFloat(true);
        return navigator;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SpellcasterCastingSpellGoal());
        goalSelector.addGoal(1, new SpawnRiftGoal());
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false));
        goalSelector.addGoal(3, new RandomStrollGoal(this, 0.6));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 3, 1));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Mob.class, 8));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return super.isInvulnerableTo(pSource) || pSource.isFire() || pSource == DamageSource.DROWN;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    public class SpawnRiftGoal extends SpellcasterUseSpellGoal {

        private static final int MOB_THRESHOLD = 16;
        private static final double DISTANCE_THRESHOLD = 16;
        private static final double MIN_DIST = 10;
        private static final double MAX_DIST = 32;
        private static final double SPAWN_CHANCE = 0.1;
        private static final int CAST_TIME = 100;
        private static final int COOLDOWN = 40;
        private static final MobEffectInstance SLOW_FALLING = new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 2);

        @Override
        public boolean canUse() {
            LivingEntity target = getTarget();
            if (!super.canUse()) {
                return false;
            } else if (target == null) {
                Raid raid = getCurrentRaid();
                if (raid == null) {
                    return false;
                }
                return raid.getTotalRaidersAlive() < MOB_THRESHOLD;
            } else if (target.distanceToSqr(position()) > DISTANCE_THRESHOLD * DISTANCE_THRESHOLD) {
                double range = getAttributeValue(Attributes.FOLLOW_RANGE);
                AABB bounds = AABB.ofSize(getEyePosition(), range * 2, range * 2, range * 2);
                return level.getEntitiesOfClass(Raider.class, bounds).size() < MOB_THRESHOLD;
            }
            return false;
        }

        @Override
        protected void performSpellCasting() {
            Vec3 pos = getRiftTarget();
            RiftCoordinationHelper.placeRandomRift((ServerLevel) level, false, pos, block -> {
                if (getRandom().nextDouble() < SPAWN_CHANCE) {
                    spawnAlly(block);
                }
            });
        }

        protected void spawnAlly(BlockPos pos) {
            EntityType<?> type = selectEntityType();
            if (type != null && type.getBaseClass().isAssignableFrom(Raider.class)) {
                Raider entity = (Raider) type.spawn((ServerLevel) level, null, null, pos, MobSpawnType.REINFORCEMENT, false, false);
                if (entity != null) {
                    entity.setPortalCooldown();
                    entity.addEffect(SLOW_FALLING);
                    entity.setTarget(getTarget());
                    Raid raid = getCurrentRaid();
                    if (Raids.canJoinRaid(entity, raid)) {
                        raid.joinRaid(raid.getGroupsSpawned(), entity, null, true);
                    }
                }
            }
        }

        @Nullable
        protected EntityType<?> selectEntityType() {
            ITag<EntityType<?>> tag = ForgeRegistries.ENTITIES.tags().getTag(MultiverseTags.CONQUEROR_SUMMON);
            return tag.getRandomElement(getRandom()).orElse(null);
        }

        protected Vec3 getRiftTarget() {
            Vec3 center = getTarget() == null ? getEyePosition() : getTarget().getEyePosition();
            return EntityUtil.randomAroundAbove(getRandom(), center, MIN_DIST, MAX_DIST);
        }

        @Override
        protected int getCastingTime() {
            return CAST_TIME;
        }

        @Override
        protected int getCastingInterval() {
            return COOLDOWN;
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
