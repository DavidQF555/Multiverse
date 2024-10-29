package io.github.davidqf555.minecraft.multiverse.client;

import io.github.davidqf555.minecraft.multiverse.client.colors.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.OptionalInt;

public final class ClientHelper {

    static ShaderInstance riftShader;

    private ClientHelper() {
    }

    public static void addDimension(ResourceKey<Level> key) {
        Minecraft.getInstance().player.connection.levels().add(key);
    }

    public static void addRiftParticles(OptionalInt from, Vec3 center) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            int index = from.orElseGet(() -> {
                int i = world.getRandom().nextInt(ServerConfigs.INSTANCE.maxDimensions.get());
                if (i >= DimensionHelper.getIndex(world.dimension())) {
                    i++;
                }
                return i;
            });
            int color = MultiverseColorHelper.getColor(world, index);
            world.addParticle(ParticleTypeRegistry.RIFT.get(), center.x(), center.y(), center.z(), FastColor.ARGB32.red(color) / 255.0, FastColor.ARGB32.green(color) / 255.0, FastColor.ARGB32.blue(color) / 255.0);
        }
    }

    public static ShaderInstance getRiftShader() {
        return riftShader;
    }

}
