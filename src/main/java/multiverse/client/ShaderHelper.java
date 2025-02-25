package multiverse.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import multiverse.common.Multiverse;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public final class ShaderHelper {

    private static final ResourceLocation RIFT_TEXTURE = new ResourceLocation(Multiverse.MOD_ID, "textures/block/rift.png");
    public static final ParticleRenderType RIFT_PARTICLE_TYPE = new ParticleRenderType() {
        @SuppressWarnings("deprecation")
        @Override
        public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShader(ShaderHelper::getRiftParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.setShaderTexture(1, RIFT_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator pTesselator) {
            pTesselator.end();
        }
    };
    public static final RenderType RIFT_VANILLA = RenderType.create("rift_vanilla", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(RIFT_TEXTURE, false, false).build())
                    .createCompositeState(false)
    );
    static ShaderInstance riftShader, riftParticleShader;
    public static final RenderType RIFT = RenderType.create("rift", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(ShaderHelper::getRiftShader))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(RIFT_TEXTURE, false, false).build())
                    .createCompositeState(false)
    );

    private ShaderHelper() {
    }

    public static ShaderInstance getRiftShader() {
        return riftShader;
    }

    public static ShaderInstance getRiftParticleShader() {
        return riftParticleShader;
    }

}
