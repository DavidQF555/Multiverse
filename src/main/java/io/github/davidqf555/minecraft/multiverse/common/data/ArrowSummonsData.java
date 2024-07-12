package io.github.davidqf555.minecraft.multiverse.common.data;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;

public class ArrowSummonsData extends SavedData {

    private static final String NAME = Multiverse.MOD_ID + "_ArrowSummons";
    private final Map<ShotData, Integer> data = new HashMap<>();

    protected ArrowSummonsData(CompoundTag tag, HolderLookup.Provider provider) {
        if (tag.contains("Data", Tag.TAG_LIST)) {
            for (Tag t : tag.getList("Data", Tag.TAG_COMPOUND)) {
                if (((CompoundTag) t).contains("Data", Tag.TAG_COMPOUND) && ((CompoundTag) t).contains("Count", Tag.TAG_INT)) {
                    ShotData shot = new ShotData(Vec3.ZERO, Vec3.ZERO, null, false);
                    shot.deserializeNBT(provider, ((CompoundTag) t).getCompound("Data"));
                    data.put(shot, ((CompoundTag) t).getInt("Count"));
                }
            }
        }
    }

    protected ArrowSummonsData() {
    }

    public static Optional<ArrowSummonsData> get(ServerLevel level) {
        return Optional.ofNullable(level.getDataStorage().get(new Factory<>(ArrowSummonsData::new, ArrowSummonsData::new, DataFixTypes.LEVEL), NAME));
    }

    public static ArrowSummonsData getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(ArrowSummonsData::new, ArrowSummonsData::new, DataFixTypes.LEVEL), NAME);
    }

    public void tick(ServerLevel level) {
        Iterator<ShotData> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            ShotData data = iterator.next();
            int count = this.data.get(data);
            if (count <= 0) {
                iterator.remove();
            } else if (level.getGameTime() % ServerConfigs.INSTANCE.spawnPeriod.get() == 0) {
                Entity shooter = level.getEntity(data.shooter);
                if (shooter instanceof LivingEntity && !shooter.isRemoved()) {
                    shoot(level, (LivingEntity) shooter, data.start, data.direction, data.fireworksOnly);
                    this.data.put(data, count - 1);
                } else {
                    iterator.remove();
                }
            }
        }
    }

    protected void addParticles(ServerLevel world, Vec3 start) {
        PacketDistributor.sendToPlayersTrackingChunk(world, new ChunkPos(BlockPos.containing(start)), new RiftParticlesPacket(Optional.empty(), start));
    }

    protected ItemStack randomFirework(RandomSource random) {
        ItemStack stack = Items.FIREWORK_ROCKET.getDefaultInstance();
        int flight = random.nextInt(1, 4);
        List<FireworkExplosion> explosions = new ArrayList<>();
        DyeColor[] options = DyeColor.values();
        int count = random.nextInt(1, 5);
        for (int i = 0; i < count; i++) {
            FireworkExplosion.Shape shape = Util.getRandom(FireworkExplosion.Shape.values(), random);
            boolean flicker = random.nextBoolean();
            boolean trail = random.nextBoolean();
            int[] colors = new int[random.nextInt(1, 9)];
            for (int j = 0; j < colors.length; j++) {
                colors[j] = options[random.nextInt(options.length)].getFireworkColor();
            }
            explosions.add(new FireworkExplosion(shape, IntList.of(colors), IntList.of(), trail, flicker));
        }
        stack.set(DataComponents.FIREWORKS, new Fireworks(flight, explosions));
        return stack;
    }

    protected AbstractArrow randomArrow(ServerLevel world, LivingEntity shooter) {
        RandomSource random = world.getRandom();
        ArrowItem item = (ArrowItem) BuiltInRegistries.ITEM.getTag(ItemTags.ARROWS)
                .map(tag -> tag.getRandomElement(random))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Holder::value)
                .filter(i -> i instanceof ArrowItem)
                .orElse(Items.ARROW);
        ItemStack stack = item.getDefaultInstance();
        if (item == Items.TIPPED_ARROW) {
            PotionContents potion = BuiltInRegistries.POTION.getRandom(random)
                    .map(PotionContents::new)
                    .orElse(PotionContents.EMPTY);
            stack.set(DataComponents.POTION_CONTENTS, potion);
        }
        ItemStack bow = (random.nextBoolean() ? Items.CROSSBOW : Items.BOW).getDefaultInstance();
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (ResourceKey<Enchantment> key : List.of(Enchantments.PUNCH, Enchantments.PIERCING, Enchantments.POWER)) {
            Holder<Enchantment> holder = world.holderOrThrow(key);
            int max = holder.value().getMaxLevel();
            enchantments.set(holder, random.nextInt(max + 1));
        }
        if (random.nextDouble() < ServerConfigs.INSTANCE.fireRate.get()) {
            enchantments.set(world.holderOrThrow(Enchantments.FLAME), 1);
        }
        EnchantmentHelper.setEnchantments(bow, enchantments.toImmutable());
        AbstractArrow arrow = item.createArrow(world, stack, shooter, bow);
        arrow.setCritArrow(true);
        return arrow;
    }

    public void add(Vec3 start, Vec3 direction, UUID shooter, int count, boolean fireworksOnly) {
        data.put(new ShotData(start, direction, shooter, fireworksOnly), count);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        data.forEach((data, count) -> {
            CompoundTag t = new CompoundTag();
            t.put("Data", data.serializeNBT(provider));
            t.putInt("Count", count);
            list.add(t);
        });
        tag.put("Data", list);
        return tag;
    }

    @Nullable
    protected Vec3 getStartPosition(ServerLevel world, Vec3 center, Vec3 direction) {
        RandomSource random = world.getRandom();
        double min = ServerConfigs.INSTANCE.minSpawnRadius.get();
        double max = ServerConfigs.INSTANCE.maxSpawnRadius.get();
        double offset = ServerConfigs.INSTANCE.spawnOffset.get();
        Vec3 start = direction.cross(new Vec3(0, 1, 0));
        if (start.lengthSqr() < 1E-8) {
            start = new Vec3(1, 0, 0);
        } else {
            start = start.normalize();
        }
        Vec3 parallel = direction.scale(direction.dot(start));
        Vec3 perp = start.subtract(parallel);
        Vec3 cross = direction.cross(perp).normalize();
        for (int i = 0; i < 16; i++) {
            double dist = random.nextDouble() * (max - min) + min;
            float angle = random.nextFloat() * Mth.TWO_PI;
            Vec3 rotate = perp.scale(Mth.cos(angle)).add(cross.scale(Mth.sin(angle) * perp.length()));
            Vec3 pos = center.add(rotate.add(parallel).scale(dist)).add(direction.scale(offset));
            BlockPos block = BlockPos.containing(pos);
            if (!world.getBlockState(block).isSolidRender(world, block)) {
                return pos;
            }
        }
        return null;
    }

    private void shoot(ServerLevel world, LivingEntity shooter, Vec3 center, Vec3 direction, boolean fireworksOnly) {
        if (!world.isClientSide()) {
            direction = direction.normalize();
            RandomSource rand = world.getRandom();
            Vec3 start = getStartPosition(world, center, direction);
            if (start != null) {
                Projectile projectile;
                if (fireworksOnly || rand.nextDouble() < ServerConfigs.INSTANCE.fireworkRate.get()) {
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

        protected ShotData(Vec3 start, Vec3 direction, UUID shooter, boolean fireworksOnly) {
            this.start = start;
            this.direction = direction.normalize();
            this.shooter = shooter;
            this.fireworksOnly = fireworksOnly;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("StartX", start.x());
            tag.putDouble("StartY", start.y());
            tag.putDouble("StartZ", start.z());
            tag.putDouble("DirectionX", direction.x());
            tag.putDouble("DirectionY", direction.y());
            tag.putDouble("DirectionZ", direction.z());
            tag.putBoolean("FireworksOnly", fireworksOnly);
            tag.putUUID("Shooter", shooter);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
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
