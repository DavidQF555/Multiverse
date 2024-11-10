package io.github.davidqf555.minecraft.multiverse.common.entities;

import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.entities.ai.EntityHurtByTargetGoal;
import io.github.davidqf555.minecraft.multiverse.common.entities.ai.EntityHurtTargetGoal;
import io.github.davidqf555.minecraft.multiverse.common.entities.ai.FollowEntityGoal;
import io.github.davidqf555.minecraft.multiverse.common.util.EntityUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DoppelgangerEntity extends PathfinderMob {

    private static final EntityDataAccessor<Optional<UUID>> ORIGINAL = SynchedEntityData.defineId(DoppelgangerEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final byte RIFT_PARTICLES_EVENT = 50;
    private static final double GEAR_RATE = 0.8;
    private static final float ENCHANT_RATE = 0.5f;

    public DoppelgangerEntity(EntityType<? extends DoppelgangerEntity> mob, Level level) {
        super(mob, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.MOVEMENT_SPEED, 0.35635);
    }

    @Nullable
    public static <T extends DoppelgangerEntity> T spawnRandom(EntityType<T> type, ServerPlayer player, BlockPos center, int minOffset, int maxOffset) {
        T entity = EntityUtil.randomSpawn(type, player.getLevel(), center, minOffset, maxOffset, MobSpawnType.REINFORCEMENT);
        if (entity != null) {
            entity.doRiftEffect();
            entity.setOriginal(player);
        }
        return entity;
    }

    public static boolean canSpawn(EntityType<? extends DoppelgangerEntity> type, ServerLevelAccessor level, MobSpawnType spawn, BlockPos pos, RandomSource rand) {
        return spawn == MobSpawnType.REINFORCEMENT;
    }

    private static TagKey<Item> getEquipmentTag(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> MultiverseTags.DOPPELGANGER_HEAD;
            case CHEST -> MultiverseTags.DOPPELGANGER_CHEST;
            case LEGS -> MultiverseTags.DOPPELGANGER_LEGS;
            case FEET -> MultiverseTags.DOPPELGANGER_FEET;
            case MAINHAND -> MultiverseTags.DOPPELGANGER_MAIN_HAND;
            case OFFHAND -> MultiverseTags.DOPPELGANGER_OFF_HAND;
        };
    }

    @Nullable
    @SuppressWarnings("deprecation")
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        populateDefaultEquipmentSlots(random, difficulty);
        populateDefaultEquipmentEnchantments(random, difficulty);
        return super.finalizeSpawn(level, difficulty, spawn, data, tag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (random.nextDouble() < GEAR_RATE) {
                ForgeRegistries.ITEMS.tags().getTag(getEquipmentTag(slot)).getRandomElement(random).map(Item::getDefaultInstance).ifPresent(stack -> setItemSlot(slot, stack));
            }
        }
    }

    @Override
    protected void populateDefaultEquipmentEnchantments(RandomSource random, DifficultyInstance difficulty) {
        enchantSpawnedWeapon(random, 4 * ENCHANT_RATE);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                enchantSpawnedArmor(random, 2 * ENCHANT_RATE, slot);
            }
        }
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slot) {
        return 0;
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
        goalSelector.addGoal(2, new FollowEntityGoal<>(this, DoppelgangerEntity::getOriginal, 12, 8, 1));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new EntityHurtByTargetGoal<>(this, DoppelgangerEntity::getOriginal));
        targetSelector.addGoal(2, new EntityHurtTargetGoal<>(this, DoppelgangerEntity::getOriginal));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, entity -> entity instanceof Enemy && !(entity instanceof Creeper)));
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        UUID original = getOriginalId();
        if (entity.getUUID().equals(original)) {
            return true;
        }
        if (entity instanceof DoppelgangerEntity && original != null && original.equals(((DoppelgangerEntity) entity).getOriginalId())) {
            return true;
        }
        return super.isAlliedTo(entity);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(ORIGINAL, Optional.empty());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        Player original = getOriginal();
        if (original == null) {
            kill();
        } else if (original.tickCount - Math.max(original.getLastHurtMobTimestamp(), original.getLastHurtByMobTimestamp()) >= ServerConfigs.INSTANCE.doppelTimeout.get()) {
            kill();
        }
    }

    protected void doRiftEffect() {
        level.broadcastEntityEvent(this, RIFT_PARTICLES_EVENT);
    }

    @Override
    protected void tickDeath() {
        if (level.isClientSide()) {
            super.tickDeath();
        } else {
            doRiftEffect();
            discard();
        }
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == RIFT_PARTICLES_EVENT) {
            ClientHelper.addRiftParticles(OptionalInt.empty(), getEyePosition());
        }
        super.handleEntityEvent(b);
    }


    @Nullable
    public Player getOriginal() {
        UUID original = getOriginalId();
        if (original != null) {
            return level.getPlayerByUUID(original);
        }
        return null;
    }

    public void setOriginal(@Nullable Player player) {
        if (player == null) {
            setOriginalId(null);
            setCustomName(null);
            setCustomNameVisible(false);
        } else {
            setOriginalId(player.getUUID());
            setCustomName(player.getDisplayName());
            setCustomNameVisible(true);
        }
    }

    @Nullable
    public UUID getOriginalId() {
        return getEntityData().get(ORIGINAL).orElse(null);
    }

    public void setOriginalId(@Nullable UUID id) {
        getEntityData().set(ORIGINAL, Optional.ofNullable(id));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Original", Tag.TAG_INT_ARRAY)) {
            setOriginalId(tag.getUUID("Original"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        UUID id = getOriginalId();
        if (id != null) {
            tag.putUUID("Original", id);
        }
    }

}
