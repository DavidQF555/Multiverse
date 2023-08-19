package io.github.davidqf555.minecraft.multiverse.common.data;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.*;

public class ArrowSummonsData extends SavedData {

    private static final String NAME = Multiverse.MOD_ID + "_ArrowSummonsData";
    private static final double FIREWORK_RATE = 0.2, FIRE_RATE = 0.2, MAX_RAD = 10, MIN_RAD = 4, OFFSET = 2;
    private static final int PERIOD = 5;
    private final Map<ShotData, Integer> data = new HashMap<>();

    protected ArrowSummonsData(CompoundTag tag) {
        if (tag.contains("Data", Tag.TAG_LIST)) {
            for (Tag t : tag.getList("Data", Tag.TAG_COMPOUND)) {
                if (((CompoundTag) t).contains("Data", Tag.TAG_COMPOUND) && ((CompoundTag) t).contains("Count", Tag.TAG_INT)) {
                    ShotData shot = new ShotData(Vec3.ZERO, Vec3.ZERO, null);
                    shot.deserializeNBT(((CompoundTag) t).getCompound("Data"));
                    data.put(shot, ((CompoundTag) t).getInt("Count"));
                }
            }
        }
    }

    protected ArrowSummonsData() {
    }

    public static Optional<ArrowSummonsData> get(ServerLevel level) {
        return Optional.ofNullable(level.getDataStorage().get(ArrowSummonsData::new, NAME));
    }

    public static ArrowSummonsData getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ArrowSummonsData::new, ArrowSummonsData::new, NAME);
    }

    public void tick(ServerLevel level) {
        Iterator<ShotData> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            ShotData data = iterator.next();
            int count = this.data.get(data);
            if (count <= 0) {
                iterator.remove();
            } else if (level.getGameTime() % PERIOD == 0) {
                LivingEntity shooter = null;
                if (data.shooter != null) {
                    Entity e = level.getEntity(data.shooter);
                    if (e instanceof LivingEntity) {
                        shooter = (LivingEntity) e;
                    }
                }
                shoot(level, shooter, data.start, data.direction);
                this.data.put(data, count - 1);
            }
        }
    }

    public void add(Vec3 start, Vec3 direction, @Nullable UUID shooter, int count) {
        data.put(new ShotData(start, direction, shooter), count);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        data.forEach((data, count) -> {
            CompoundTag t = new CompoundTag();
            t.put("Data", data.serializeNBT());
            t.putInt("Count", count);
            list.add(t);
        });
        tag.put("Data", list);
        return tag;
    }

    protected Vec3 getStartPosition(Random random, Vec3 center, Vec3 direction) {
        double dist = random.nextDouble(MIN_RAD, MAX_RAD);
        float angle = random.nextFloat() * Mth.PI * 2;
        Vec3 start = direction.cross(new Vec3(0, 1, 0));
        if (start.lengthSqr() == 0) {
            start = new Vec3(1, 0, 0);
        } else {
            start = start.normalize();
        }
        Vec3 parallel = direction.scale(direction.dot(start));
        Vec3 perp = start.subtract(parallel);
        Vec3 cross = direction.cross(perp).normalize();
        Vec3 rotate = perp.scale(Mth.cos(angle)).add(cross.scale(Mth.sin(angle) * perp.length()));
        return center.add(rotate.add(parallel).scale(dist)).add(direction.reverse().scale(OFFSET));
    }

    private void shoot(ServerLevel world, @Nullable LivingEntity shooter, Vec3 center, Vec3 direction) {
        if (!world.isClientSide()) {
            direction = direction.normalize();
            Random rand = world.getRandom();
            Vec3 start = getStartPosition(rand, center, direction);
            Projectile projectile;
            if (rand.nextDouble() < FIREWORK_RATE) {
                ItemStack stack = Items.FIREWORK_ROCKET.getDefaultInstance();
                projectile = new FireworkRocketEntity(world, stack, shooter, start.x(), start.y(), start.z(), true);
            } else {
                projectile = ((ArrowItem) Items.ARROW).createArrow(world, Items.ARROW.getDefaultInstance(), shooter);
                projectile.setPos(start);
                ((AbstractArrow) projectile).setCritArrow(true);
                ((AbstractArrow) projectile).setShotFromCrossbow(rand.nextBoolean());
                ((AbstractArrow) projectile).setKnockback(rand.nextInt(Enchantments.PUNCH_ARROWS.getMaxLevel() + 1));
                int power = rand.nextInt(Enchantments.POWER_ARROWS.getMaxLevel() + 1);
                if (power > 0) {
                    ((AbstractArrow) projectile).setBaseDamage(((AbstractArrow) projectile).getBaseDamage() + power * 0.5 + 0.5);
                }
                if (rand.nextDouble() < FIRE_RATE) {
                    projectile.setSecondsOnFire(100);
                }
                ((AbstractArrow) projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            float multiplier = rand.nextFloat() * 2 + 1.5f;
            float variation = rand.nextFloat() * 0.4f + 0.8f;
            projectile.shoot(direction.x(), direction.y(), direction.z(), multiplier, variation);
            world.addFreshEntity(projectile);
            world.playSound(null, start.x(), start.y(), start.z(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1, rand.nextFloat() * 0.3f + 0.85f);
        }
    }

    protected static class ShotData implements INBTSerializable<CompoundTag> {

        protected Vec3 start, direction;
        protected UUID shooter;

        protected ShotData(Vec3 start, Vec3 direction, @Nullable UUID shooter) {
            this.start = start;
            this.direction = direction.normalize();
            this.shooter = shooter;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("StartX", start.x());
            tag.putDouble("StartY", start.y());
            tag.putDouble("StartZ", start.z());
            tag.putDouble("DirectionX", direction.x());
            tag.putDouble("DirectionY", direction.y());
            tag.putDouble("DirectionZ", direction.z());
            if (shooter != null) {
                tag.putUUID("Shooter", shooter);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            if (nbt.contains("StartX", Tag.TAG_DOUBLE) && nbt.contains("StartY", Tag.TAG_DOUBLE) && nbt.contains("StartZ", Tag.TAG_DOUBLE)) {
                start = new Vec3(nbt.getDouble("StartX"), nbt.getDouble("StartY"), nbt.getDouble("StartZ"));
            }
            if (nbt.contains("DirectionX", Tag.TAG_DOUBLE) && nbt.contains("DirectionY", Tag.TAG_DOUBLE) && nbt.contains("DirectionZ", Tag.TAG_DOUBLE)) {
                direction = new Vec3(nbt.getDouble("DirectionX"), nbt.getDouble("DirectionY"), nbt.getDouble("DirectionZ"));
            }
            if (nbt.contains("Shooter", Tag.TAG_INT_ARRAY)) {
                shooter = nbt.getUUID("Shooter");
            }
        }

    }

}
