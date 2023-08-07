package io.github.davidqf555.minecraft.multiverse.common.entities;

import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.items.RiftSwordItem;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
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
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CollectorEntity extends SpellcasterIllager {

    private final ServerBossEvent bar;
    private int from;

    public CollectorEntity(EntityType<? extends CollectorEntity> type, Level world) {
        super(type, world);
        moveControl = new FlyingMoveControl(this, 90, true);
        bar = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150)
                .add(Attributes.FLYING_SPEED, 2)
                .add(Attributes.FOLLOW_RANGE, 40)
                .add(Attributes.ATTACK_DAMAGE, 5);
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
        setItemInHand(InteractionHand.MAIN_HAND, ItemRegistry.BOUNDLESS_BLADE.get().getDefaultInstance());
    }

    @Override
    protected void populateDefaultEquipmentEnchantments(RandomSource random, DifficultyInstance p_21462_) {
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader reader) {
        return 0;
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation navigator = new FlyingPathNavigation(this, world);
        navigator.setCanFloat(true);
        return navigator;
    }

    @Override
    public boolean hurt(DamageSource p_37849_, float p_37850_) {
        return super.hurt(p_37849_, p_37850_);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new SpellcasterCastingSpellGoal());
        goalSelector.addGoal(2, new CreateRiftGoal());
        goalSelector.addGoal(3, new EnterRiftGoal(1, 16, 1));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
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
            ((CollectorEntity) entity).setFrom(DimensionHelper.getIndex(level().dimension()));
            entity.setPortalCooldown();
        }
        return entity;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean ret = super.doHurtTarget(target);
        if (ret && target instanceof LivingEntity) {
            EntityUtil.randomTeleport((LivingEntity) target, target.position(), 2, 8, true);
        }
        return ret;
    }

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
        if (hasCustomName()) {
            bar.setName(getDisplayName());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("From", from);
    }

    private class CreateRiftGoal extends SpellcasterUseSpellGoal {

        private CreateRiftGoal() {
            nextAttackTickCount = tickCount + getCastingInterval();
        }

        @Override
        public boolean canUse() {
            return super.canUse() && getTarget() != null;
        }

        @Override
        protected void performSpellCasting() {
            float angle = getMainArm() == HumanoidArm.RIGHT ? 45 : -45;
            Vec3 look = getLookAngle();
            Vec3 start = getEyePosition().add(look);
            RiftSwordItem.slash((ServerLevel) level(), start, look, 4, 3, 15, angle);
        }

        @Override
        protected int getCastingTime() {
            return 100;
        }

        @Override
        protected int getCastingInterval() {
            return 1200;
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
            return !isOnPortalCooldown() && super.canUse();
        }
    }

}
