package io.github.davidqf555.minecraft.multiverse.client;

import io.github.davidqf555.minecraft.multiverse.client.colors.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public final class ClientHelper {

    private ClientHelper() {
    }

    public static void addRiftParticles(Vec3 center, @Nullable ResourceKey<Level> dim) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            if (dim == null) {
                dim = DimensionHelper.randomMultiverseDimension(world.getRandom(), world.dimension());
            }
            int color = MultiverseColorHelper.getColors(dim, 1)[0];
            world.addParticle(ParticleTypeRegistry.RIFT.get(), center.x(), center.y(), center.z(), FastColor.ARGB32.red(color) / 255.0, FastColor.ARGB32.green(color) / 255.0, FastColor.ARGB32.blue(color) / 255.0);
        }
    }

    public static void addRiftExplosionParticles(Vec3 center, @Nullable ResourceKey<Level> dim) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            if (dim == null) {
                dim = DimensionHelper.randomMultiverseDimension(world.getRandom(), world.dimension());
            }
            int color = MultiverseColorHelper.getColors(dim, 1)[0];
            world.addParticle(ParticleTypeRegistry.RIFT_EXPLOSION_EMITTER.get(), center.x(), center.y(), center.z(), FastColor.ARGB32.red(color) / 255.0, FastColor.ARGB32.green(color) / 255.0, FastColor.ARGB32.blue(color) / 255.0);
        }
    }

}
