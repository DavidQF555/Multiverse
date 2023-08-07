package io.github.davidqf555.minecraft.multiverse.common.entities;

import io.github.davidqf555.minecraft.multiverse.client.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.entities.ai.EntityHurtByTargetGoal;
import io.github.davidqf555.minecraft.multiverse.common.entities.ai.EntityHurtTargetGoal;
import io.github.davidqf555.minecraft.multiverse.common.entities.ai.FollowEntityGoal;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
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
import java.util.Optional;
import java.util.UUID;

public class DoppelgangerEntity extends PathfinderMob {

    private static final EntityDataAccessor<Optional<UUID>> ORIGINAL = SynchedEntityData.defineId(DoppelgangerEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final int TIMEOUT = 600, PARTICLES_COUNT = 50;
    private static final byte RIFT_PARTICLES_EVENT = 50;
    private static final double GEAR_RATE = 0.8;
    private static final float ENCHANT_RATE = 0.5f;

    public DoppelgangerEntity(EntityType<? extends DoppelgangerEntity> mob, Level level) {
        super(mob, level);
        setCustomNameVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.MOVEMENT_SPEED, 0.35635);
    }

    public static <T extends DoppelgangerEntity> T spawnRandom(EntityType<T> type, ServerPlayer player, BlockPos center, int minOffset, int maxOffset) {
        RandomSource rand = player.getRandom();
        int dX = rand.nextInt(maxOffset - minOffset + 1) + minOffset;
        if (rand.nextBoolean()) {
            dX *= -1;
        }
        int dY = rand.nextInt(maxOffset - minOffset + 1) + minOffset;
        if (rand.nextBoolean()) {
            dY *= -1;
        }
        int dZ = rand.nextInt(maxOffset - minOffset + 1) + minOffset;
        if (rand.nextBoolean()) {
            dZ *= -1;
        }
        BlockPos pos = center.offset(dX, dY, dZ);
        T entity = type.spawn(player.getLevel(), null, player, pos, MobSpawnType.MOB_SUMMONED, false, false);
        if (entity != null) {
            entity.randomTeleport(entity.getX(), entity.getY(), entity.getZ(), false);
            entity.doRiftEffect();
            entity.setOriginal(player);
        }
        return entity;
    }

    private static TagKey<Item> getEquipmentTag(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> TagRegistry.DOPPELGANGER_HEAD;
            case CHEST -> TagRegistry.DOPPELGANGER_CHEST;
            case LEGS -> TagRegistry.DOPPELGANGER_LEGS;
            case FEET -> TagRegistry.DOPPELGANGER_FEET;
            case MAINHAND -> TagRegistry.DOPPELGANGER_MAIN_HAND;
            case OFFHAND -> TagRegistry.DOPPELGANGER_OFF_HAND;
        };
    }

    @Nullable
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
        } else if (Math.min(original.tickCount - original.getLastHurtMobTimestamp(), original.tickCount - original.getLastHurtByMobTimestamp()) >= TIMEOUT) {
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
            int color = MultiverseColorHelper.getColor(level, random.nextInt(ServerConfigs.INSTANCE.maxDimensions.get() + 1));
            for (int i = 0; i < PARTICLES_COUNT; i++) {
                level.addParticle(ParticleTypeRegistry.RIFT.get(), getRandomX(1), getRandomY(), getRandomZ(1), FastColor.ARGB32.red(color) / 255.0, FastColor.ARGB32.green(color) / 255.0, FastColor.ARGB32.blue(color) / 255.0);
            }
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
        } else {
            setOriginalId(player.getUUID());
            setCustomName(player.getDisplayName());
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
