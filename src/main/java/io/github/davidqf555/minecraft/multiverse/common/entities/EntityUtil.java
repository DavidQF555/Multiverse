package io.github.davidqf555.minecraft.multiverse.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public final class EntityUtil {

    private EntityUtil() {
    }

    public static boolean randomTeleport(LivingEntity entity, Vec3 center, double min, double max, boolean effect) {
        RandomSource rand = entity.getRandom();
        for (int i = 0; i < 16; i++) {
            double dX = rand.nextDouble() * (max - min) + min;
            double dY = rand.nextDouble() * (max - min) + min;
            double dZ = rand.nextDouble() * (max - min) + min;
            if (rand.nextBoolean()) {
                dX *= -1;
            }
            if (rand.nextBoolean()) {
                dY *= -1;
            }
            if (rand.nextBoolean()) {
                dZ *= -1;
            }
            if (entity.randomTeleport(center.x() + dX, center.y() + dY, center.z() + dZ, effect)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static <T extends Entity> T randomSpawn(EntityType<T> type, ServerLevel world, BlockPos center, int min, int max, MobSpawnType spawn) {
        T entity = type.create(world, null, center, spawn, false, false);
        if (entity != null) {
            RandomSource rand = world.getRandom();
            for (int i = 0; i < 50; i++) {
                int dX = rand.nextInt(min, max + 1);
                if (rand.nextBoolean()) {
                    dX *= -1;
                }
                int dY = rand.nextInt(min, max + 1);
                if (rand.nextBoolean()) {
                    dY *= -1;
                }
                int dZ = rand.nextInt(min, max + 1);
                if (rand.nextBoolean()) {
                    dZ *= -1;
                }
                BlockPos pos = center.offset(dX, dY, dZ);
                if (SpawnPlacements.getPlacementType(type).isSpawnPositionOk(world, pos, type) && world.noCollision(type.getSpawnAABB(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5))) {
                    entity.setPos(Vec3.atBottomCenterOf(pos));
                    world.addFreshEntityWithPassengers(entity);
                    return entity;
                }
            }
        }
        return null;
    }

}
