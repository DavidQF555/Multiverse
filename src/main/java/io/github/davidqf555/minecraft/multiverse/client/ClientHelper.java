package io.github.davidqf555.minecraft.multiverse.client;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ClientHelper {

    private ClientHelper() {
    }

    public static void addDimension(ResourceKey<Level> key) {
        Minecraft.getInstance().player.connection.levels().add(key);
    }

    public static void addParticles(ParticleOptions particle, Vec3 center, Vec3 speed, double centerVariation, double speedVariation, int count) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            RandomSource rand = world.getRandom();
            for (int i = 0; i < count; i++) {
                world.addParticle(particle, center.x() + rand.nextGaussian() * centerVariation, center.y() + rand.nextGaussian() * centerVariation, center.z() + rand.nextGaussian() * centerVariation, speed.x() + rand.nextGaussian() * speedVariation, speed.y() + rand.nextGaussian() * speedVariation, speed.z() + rand.nextGaussian() * speedVariation);
            }
        }
    }

    public static void addRiftParticles(Vec3 center, double variation, int count) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            int index = world.getRandom().nextInt(ServerConfigs.INSTANCE.maxDimensions.get());
            if (index >= DimensionHelper.getIndex(world.dimension())) {
                index++;
            }
            int color = MultiverseColorHelper.getColor(world, index);
            addParticles(ParticleTypeRegistry.RIFT.get(), center, new Vec3(FastColor.ARGB32.red(color) / 255.0, FastColor.ARGB32.green(color) / 255.0, FastColor.ARGB32.blue(color) / 255.0), variation, 0, count);
        }
    }

}
