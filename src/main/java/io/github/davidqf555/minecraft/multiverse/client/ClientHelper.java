package io.github.davidqf555.minecraft.multiverse.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.davidqf555.minecraft.multiverse.client.colors.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class ClientHelper {

    public static final ResourceLocation RIFT = ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "textures/block/rift.png");
    public static final ParticleRenderType RIFT_PARTICLE = (tesselator, textureManager) -> {
        RenderSystem.depthMask(true);
        RenderSystem.setShader(ClientHelper::getRiftParticleShader);
        RenderSystem.setShaderTexture(1, RIFT);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
    };

    static ShaderInstance riftShader, riftParticleShader;

    private ClientHelper() {
    }

    public static void addRiftParticles(Optional<ResourceKey<Level>> dim, Vec3 center) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            ResourceKey<Level> from = dim.orElseGet(() -> DimensionHelper.randomMultiverseDimension(world.getRandom(), Optional.of(world.dimension())));
            int color = MultiverseColorHelper.getColor(world, from);
            world.addParticle(ParticleTypeRegistry.RIFT.get(), center.x(), center.y(), center.z(), FastColor.ARGB32.red(color) / 255.0, FastColor.ARGB32.green(color) / 255.0, FastColor.ARGB32.blue(color) / 255.0);
        }
    }

    public static void addRiftExplosionParticles(Optional<ResourceKey<Level>> dim, Vec3 center) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            ResourceKey<Level> from = dim.orElseGet(() -> DimensionHelper.randomMultiverseDimension(world.getRandom(), Optional.of(world.dimension())));
            int color = MultiverseColorHelper.getColor(world, from);
            world.addParticle(ParticleTypeRegistry.RIFT_EXPLOSION_EMITTER.get(), center.x(), center.y(), center.z(), FastColor.ARGB32.red(color) / 255.0, FastColor.ARGB32.green(color) / 255.0, FastColor.ARGB32.blue(color) / 255.0);
        }
    }

    public static ShaderInstance getRiftShader() {
        return riftShader;
    }

    public static ShaderInstance getRiftParticleShader() {
        return riftParticleShader;
    }

}
