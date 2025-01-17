package io.github.davidqf555.minecraft.multiverse.client.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.davidqf555.minecraft.multiverse.client.ClientConfigs;
import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RiftParticle extends SimpleAnimatedParticle {

    private static final ParticleRenderType TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShader(ClientHelper::getRiftParticleShader);
            RenderSystem.setShaderTexture(1, TheEndPortalRenderer.END_PORTAL_LOCATION);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator pTesselator) {
            pTesselator.end();
        }
    };

    public RiftParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z, sprites, 0);
        lifetime = 10 + random.nextInt(6);
        hasPhysics = false;
        scale(10);
        setAlpha((float) (double) ClientConfigs.INSTANCE.riftMaxOpacity.get());
        setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ClientConfigs.INSTANCE.vanillaOnly.get() ? super.getRenderType() : TYPE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dX, double dY, double dZ) {
            RiftParticle particle = new RiftParticle(level, x, y, z, sprites);
            particle.setColor((float) dX, (float) dY, (float) dZ);
            return particle;
        }

    }

}
