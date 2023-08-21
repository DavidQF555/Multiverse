package io.github.davidqf555.minecraft.multiverse.common.data;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class ArrowSummonsData extends SavedData {

    private static final String NAME = Multiverse.MOD_ID + "_ArrowSummonsData";
    private static final double FIREWORK_RATE = 0.2, FIRE_RATE = 0.2, TIPPED_RATE = 0.3, SPECTRAL_RATE = 0.1, MAX_RAD = 10, MIN_RAD = 4, OFFSET = 2, PARTICLES_OFFSET = 0.35;
    private static final int PERIOD = 5, PARTICLES = 100, TRIES = 16;
    private final Map<ShotData, Integer> data = new HashMap<>();

    protected ArrowSummonsData(CompoundTag tag) {
        if (tag.contains("Data", Tag.TAG_LIST)) {
            for (Tag t : tag.getList("Data", Tag.TAG_COMPOUND)) {
                if (((CompoundTag) t).contains("Data", Tag.TAG_COMPOUND) && ((CompoundTag) t).contains("Count", Tag.TAG_INT)) {
                    ShotData shot = new ShotData(Vec3.ZERO, Vec3.ZERO, null, false);
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
                shoot(level, shooter, data.start, data.direction, data.fireworksOnly);
                this.data.put(data, count - 1);
            }
        }
    }

    protected void addParticles(ServerLevel world, Vec3 start) {
        Multiverse.CHANNEL.send(PacketDistributor.DIMENSION.with(world::dimension), new RiftParticlesPacket(start, PARTICLES_OFFSET, PARTICLES));
    }

    protected ItemStack randomFirework(Random random) {
        ItemStack stack = Items.FIREWORK_ROCKET.getDefaultInstance();
        CompoundTag tag = stack.getOrCreateTagElement(FireworkRocketItem.TAG_FIREWORKS);
        tag.putByte(FireworkRocketItem.TAG_FLIGHT, (byte) random.nextInt(1, 9));
        ListTag explosions = new ListTag();
        DyeColor[] options = DyeColor.values();
        int count = random.nextInt(1, 5);
        for (int i = 0; i < count; i++) {
            CompoundTag explosion = new CompoundTag();
            explosion.putByte(FireworkRocketItem.TAG_EXPLOSION_TYPE, (byte) random.nextInt(FireworkRocketItem.Shape.values().length));
            explosion.putBoolean(FireworkRocketItem.TAG_EXPLOSION_FLICKER, random.nextBoolean());
            explosion.putBoolean(FireworkRocketItem.TAG_EXPLOSION_TRAIL, random.nextBoolean());
            int[] colors = new int[random.nextInt(1, 9)];
            for (int j = 0; j < colors.length; j++) {
                colors[j] = options[random.nextInt(options.length)].getFireworkColor();
            }
            explosion.putIntArray(FireworkRocketItem.TAG_EXPLOSION_COLORS, colors);
            explosions.add(explosion);
        }
        tag.put(FireworkRocketItem.TAG_EXPLOSIONS, explosions);
        return stack;
    }

    protected AbstractArrow randomArrow(ServerLevel world, @Nullable LivingEntity shooter) {
        Random random = world.getRandom();
        ItemStack stack = Items.ARROW.getDefaultInstance();
        if (random.nextDouble() < TIPPED_RATE) {
            List<Potion> potions = new ArrayList<>(ForgeRegistries.POTIONS.getValues());
            stack = PotionUtils.setPotion(Items.TIPPED_ARROW.getDefaultInstance(), potions.get(random.nextInt(potions.size())));
        } else if (random.nextDouble() < SPECTRAL_RATE) {
            stack = Items.SPECTRAL_ARROW.getDefaultInstance();
        }
        AbstractArrow arrow = ((ArrowItem) stack.getItem()).createArrow(world, stack, shooter);
        arrow.setCritArrow(true);
        arrow.setShotFromCrossbow(random.nextBoolean());
        arrow.setKnockback(random.nextInt(Enchantments.PUNCH_ARROWS.getMaxLevel() + 1));
        int power = random.nextInt(Enchantments.POWER_ARROWS.getMaxLevel() + 1);
        if (power > 0) {
            arrow.setBaseDamage(arrow.getBaseDamage() + power * 0.5 + 0.5);
        }
        if (random.nextDouble() < FIRE_RATE) {
            arrow.setSecondsOnFire(100);
        }
        return arrow;
    }

    public void add(Vec3 start, Vec3 direction, @Nullable UUID shooter, int count, boolean fireworksOnly) {
        data.put(new ShotData(start, direction, shooter, fireworksOnly), count);
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

    @Nullable
    protected Vec3 getStartPosition(ServerLevel world, Vec3 center, Vec3 direction) {
        Random random = world.getRandom();
        for (int i = 0; i < TRIES; i++) {
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
            Vec3 pos = center.add(rotate.add(parallel).scale(dist)).add(direction.reverse().scale(OFFSET));
            BlockPos block = new BlockPos(pos);
            if (!world.getBlockState(block).isSolidRender(world, block)) {
                return pos;
            }
        }
        return null;
    }

    private void shoot(ServerLevel world, @Nullable LivingEntity shooter, Vec3 center, Vec3 direction, boolean fireworksOnly) {
        if (!world.isClientSide()) {
            direction = direction.normalize();
            Random rand = world.getRandom();
            Vec3 start = getStartPosition(world, center, direction);
            if (start != null) {
                Projectile projectile;
                if (fireworksOnly || rand.nextDouble() < FIREWORK_RATE) {
                    projectile = new FireworkRocketEntity(world, randomFirework(rand), shooter, start.x(), start.y(), start.z(), true);
                } else {
                    projectile = randomArrow(world, shooter);
                    projectile.setPos(start);
                    ((AbstractArrow) projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                float multiplier = rand.nextFloat() * 2 + 1.5f;
                float variation = rand.nextFloat() * 0.4f + 0.8f;
                projectile.shoot(direction.x(), direction.y(), direction.z(), multiplier, variation);
                world.addFreshEntity(projectile);
                addParticles(world, start);
                world.playSound(null, start.x(), start.y(), start.z(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1, rand.nextFloat() * 0.3f + 0.85f);
            }
        }
    }

    protected static class ShotData implements INBTSerializable<CompoundTag> {

        protected Vec3 start, direction;
        protected UUID shooter;
        protected boolean fireworksOnly;

        protected ShotData(Vec3 start, Vec3 direction, @Nullable UUID shooter, boolean fireworksOnly) {
            this.start = start;
            this.direction = direction.normalize();
            this.shooter = shooter;
            this.fireworksOnly = fireworksOnly;
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
            tag.putBoolean("FireworksOnly", fireworksOnly);
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
            if (nbt.contains("FireworksOnly", Tag.TAG_BYTE)) {
                fireworksOnly = nbt.getBoolean("FireworksOnly");
            }
        }

    }

}