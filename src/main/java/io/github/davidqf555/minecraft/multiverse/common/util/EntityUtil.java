package io.github.davidqf555.minecraft.multiverse.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Set;

public final class EntityUtil {

    private static final int TRIES = 32;

    private EntityUtil() {
    }

    public static Vec3 getRandomSpawnAbove(ServerLevel world, RandomSource rand, Vec3 center, double max, double minY, double maxY, Set<EntityType<?>> types) {
        again:
        for (int i = 0; i < TRIES; i++) {
            Vec3 pos = randomAroundAbove(rand, center, max, minY, maxY);
            for (EntityType<?> type : types) {
                if (!canSpawnPosition(world, pos, type)) {
                    continue again;
                }
            }
            return pos;
        }
        return randomAroundAbove(rand, center, max, minY, maxY);
    }

    public static Vec3 randomAround(RandomSource rand, Vec3 center, double min, double max) {
        double a = rand.nextDouble() * 2 * Math.PI;
        double b = rand.nextDouble() * Math.PI - Math.PI / 2;
        double dist = rand.nextDouble() * (max - min) + min;
        double dX = Math.sin(a) * Math.cos(b) * dist;
        double dZ = Math.cos(a) * Math.cos(b) * dist;
        double dY = Math.sin(b) * dist;
        return center.add(dX, dY, dZ);
    }

    public static Vec3 randomAroundAbove(RandomSource rand, Vec3 center, double max, double minY, double maxY) {
        double dY = rand.nextDouble() * (maxY - minY) + minY;
        double angle = rand.nextDouble() * 2 * Math.PI;
        double dist = rand.nextDouble() * max;
        double dX = Math.cos(angle) * dist;
        double dZ = Math.sin(angle) * dist;
        return center.add(dX, dY, dZ);
    }

    public static boolean randomTeleport(LivingEntity entity, Vec3 center, double min, double max, boolean effect) {
        RandomSource rand = entity.getRandom();
        for (int i = 0; i < TRIES; i++) {
            Vec3 pos = randomAround(rand, center, min, max);
            if (entity.randomTeleport(pos.x(), pos.y(), pos.z(), effect)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static <T extends Entity> T randomSpawn(EntityType<T> type, ServerLevel world, BlockPos center, int min, int max, MobSpawnType spawn) {
        RandomSource rand = world.getRandom();
        for (int i = 0; i < TRIES; i++) {
            BlockPos block = BlockPos.containing(randomAround(rand, Vec3.atBottomCenterOf(center), min, max));
            if (SpawnPlacements.getPlacementType(type).canSpawnAt(world, block, type) && canSpawnPosition(world, Vec3.atBottomCenterOf(block), type)) {
                T entity = type.create(world, null, null, block, spawn, false, false);
                if (entity != null) {
                    world.addFreshEntityWithPassengers(entity);
                    return entity;
                }
            }
        }
        return null;
    }

    public static boolean canSpawnPosition(ServerLevel world, Vec3 pos, EntityType<?> type) {
        AABB bounds = type.getAABB(pos.x(), pos.y(), pos.z());
        return world.noCollision(bounds);
    }

}
