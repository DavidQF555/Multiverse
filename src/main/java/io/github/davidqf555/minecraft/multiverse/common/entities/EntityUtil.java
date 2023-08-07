package io.github.davidqf555.minecraft.multiverse.common.entities;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

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

}
