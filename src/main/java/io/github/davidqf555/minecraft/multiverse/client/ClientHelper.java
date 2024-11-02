package io.github.davidqf555.minecraft.multiverse.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import io.github.davidqf555.minecraft.multiverse.client.colors.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class ClientHelper {

    private static final ShaderProgram RIFT_SHADER = new ShaderProgram(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "core/rift"), DefaultVertexFormat.POSITION_COLOR, ShaderDefines.EMPTY);

    private ClientHelper() {
    }

    public static void addDimension(ResourceKey<Level> key) {
        Minecraft.getInstance().player.connection.levels().add(key);
    }

    public static void addRiftParticles(Optional<Integer> from, Vec3 center) {
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
            world.addParticle(ParticleTypeRegistry.RIFT.get(), center.x(), center.y(), center.z(), ARGB.red(color) / 255.0, ARGB.green(color) / 255.0, ARGB.blue(color) / 255.0);
        }
    }

    public static ShaderProgram getRiftShader() {
        return RIFT_SHADER;
    }

}
