package io.github.davidqf555.minecraft.multiverse.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public final class ShaderHelper {

    public static final ShaderProgram RIFT_SHADER = new ShaderProgram(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "core/rift"), DefaultVertexFormat.POSITION_COLOR, ShaderDefines.EMPTY);
    public static final ShaderProgram RIFT_PARTICLE_SHADER = new ShaderProgram(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "core/rift_particle"), DefaultVertexFormat.PARTICLE, ShaderDefines.EMPTY);
    private static final ResourceLocation RIFT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "textures/block/rift.png");
    @SuppressWarnings("deprecation")
    public static final ParticleRenderType RIFT_PARTICLE_TYPE = (tesselator, textureManager) -> {
        RenderSystem.depthMask(true);
        RenderSystem.setShader(RIFT_PARTICLE_SHADER);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        RenderSystem.setShaderTexture(1, RIFT_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
    };
    public static final RenderType RIFT_VANILLA = RenderType.create("rift_vanilla", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(RIFT_TEXTURE, false, false).build())
                    .createCompositeState(false)
    );
    public static final RenderType RIFT = RenderType.create("rift", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(RIFT_SHADER))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(RIFT_TEXTURE, false, false).build())
                    .createCompositeState(false)
    );

    private ShaderHelper() {
    }

}
