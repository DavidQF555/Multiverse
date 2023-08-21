package io.github.davidqf555.minecraft.multiverse.common.entities;

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

    public static boolean randomTeleport(LivingEntity entity, Vec3 center, double min, double max, boolean effect) {
        Random rand = entity.getRandom();
        for (int i = 0; i < 16; i++) {
            double dX = rand.nextDouble(min, max);
            double dY = rand.nextDouble(min, max);
            double dZ = rand.nextDouble(min, max);
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
        T entity = type.create(world, null, null, null, center, spawn, false, false);
        if (entity != null) {
            Random rand = world.getRandom();
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
                if (SpawnPlacements.getPlacementType(type).canSpawnAt(world, pos, type) && world.noCollision(type.getAABB(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5))) {
                    entity.setPos(Vec3.atBottomCenterOf(pos));
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
