package io.github.davidqf555.minecraft.multiverse.client;

import io.github.davidqf555.minecraft.multiverse.client.colors.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;

public final class ClientHelper {

    private static final Random RANDOM = new Random();

    private ClientHelper() {
    }

    public static void addRiftParticles(Vec3 center, @Nullable ResourceKey<Level> dim) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            int color;
            if (dim == null) {
                color = MultiverseColorHelper.getColors(RANDOM, 1)[0];
            } else {
                color = MultiverseColorHelper.getColors(dim, 1)[0];
            }
            world.addParticle(ParticleTypeRegistry.RIFT.get(), center.x(), center.y(), center.z(), FastColor.ARGB32.red(color) / 255.0, FastColor.ARGB32.green(color) / 255.0, FastColor.ARGB32.blue(color) / 255.0);
        }
    }

    public static void addRiftExplosionParticles(Vec3 center, @Nullable ResourceKey<Level> dim) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            int color;
            if (dim == null) {
                color = MultiverseColorHelper.getColors(RANDOM, 1)[0];
            } else {
                color = MultiverseColorHelper.getColors(dim, 1)[0];
            }
            world.addParticle(ParticleTypeRegistry.RIFT_EXPLOSION_EMITTER.get(), center.x(), center.y(), center.z(), FastColor.ARGB32.red(color) / 255.0, FastColor.ARGB32.green(color) / 255.0, FastColor.ARGB32.blue(color) / 255.0);
        }
    }

    public static void playWarpSound(Vec3 pos, SoundSource source) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            world.playLocalSound(pos.x(), pos.y(), pos.z(), SoundEvents.ENDERMAN_TELEPORT, source, 1, 1, false);
        }
    }

}
