package io.github.davidqf555.minecraft.multiverse.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Random;

public final class EntityUtil {

    private EntityUtil() {
    }

    public static Vec3 randomAround(Random rand, Vec3 center, double min, double max) {
        double a = rand.nextDouble(2 * Math.PI);
        double b = rand.nextDouble(Math.PI) - Math.PI / 2;
        double dist = rand.nextDouble() * (max - min) + min;
        double dX = Math.sin(a) * Math.cos(b) * dist;
        double dZ = Math.cos(a) * Math.cos(b) * dist;
        double dY = Math.sin(b) * dist;
        return center.add(dX, dY, dZ);
    }

    public static Vec3 randomAroundAbove(Random rand, Vec3 center, double max, double minY, double maxY) {
        double dY = rand.nextDouble() * (maxY - minY) + minY;
        double angle = rand.nextDouble(2 * Math.PI);
        double dist = rand.nextDouble() * max;
        double dX = Math.cos(angle) * dist;
        double dZ = Math.sin(angle) * dist;
        return center.add(dX, dY, dZ);
    }

    public static boolean randomTeleport(LivingEntity entity, Vec3 center, double min, double max, boolean effect) {
        Random rand = entity.getRandom();
        for (int i = 0; i < 16; i++) {
            Vec3 pos = randomAround(rand, center, min, max);
            if (entity.randomTeleport(pos.x(), pos.y(), pos.z(), effect)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static <T extends Entity> T randomSpawn(EntityType<T> type, ServerLevel world, BlockPos center, int min, int max, MobSpawnType spawn) {
        T entity = type.create(world, null, null, null, center, spawn, false, false);
        if (entity != null) {
            Random rand = world.getRandom();
            for (int i = 0; i < 50; i++) {
                BlockPos block = new BlockPos(randomAround(rand, Vec3.atBottomCenterOf(center), min, max));
                Vec3 pos = Vec3.atBottomCenterOf(block);
                if (SpawnPlacements.getPlacementType(type).canSpawnAt(world, block, type) && world.noCollision(type.getAABB(pos.x(), pos.y(), pos.z()))) {
                    entity.setPos(pos);
                    if (!(entity instanceof Mob) || !ForgeEventFactory.doSpecialSpawn((Mob) entity, (LevelAccessor) world, (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), null, spawn)) {
                        world.addFreshEntityWithPassengers(entity);
                        return entity;
                    }
                }
            }
        }
        return null;
    }

}
