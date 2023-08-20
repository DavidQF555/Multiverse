package io.github.davidqf555.minecraft.multiverse.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public final class ClientHelper {

    private ClientHelper() {
    }

    public static void addDimension(ResourceKey<Level> key) {
        Minecraft.getInstance().player.connection.levels().add(key);
    }

    public static void addParticles(ParticleOptions particle, Vec3 center, Vec3 speed, double centerVariation, double speedVariation, int count) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            Random rand = world.getRandom();
            for (int i = 0; i < count; i++) {
                world.addParticle(particle, center.x() + rand.nextGaussian() * centerVariation, center.y() + rand.nextGaussian() * centerVariation, center.z() + rand.nextGaussian() * centerVariation, speed.x() + rand.nextGaussian() * speedVariation, speed.y() + rand.nextGaussian() * speedVariation, speed.z() + rand.nextGaussian() * speedVariation);
            }
        }
    }

}
