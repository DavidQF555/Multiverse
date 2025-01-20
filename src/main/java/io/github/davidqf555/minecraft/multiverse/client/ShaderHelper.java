package io.github.davidqf555.minecraft.multiverse.client;

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
    @SuppressWarnings("deprecation")
    public static final RenderType RIFT_PARTICLE = RenderType.create(
            "rift_particle",
            DefaultVertexFormat.PARTICLE,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(RIFT_PARTICLE_SHADER))
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(TextureAtlas.LOCATION_PARTICLES, false, false)
                            .add(RIFT_TEXTURE, false, false)
                            .build())
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderStateShard.PARTICLES_TARGET)
                    .createCompositeState(false)
    );
    public static final ParticleRenderType RIFT_PARTICLE_TYPE = new ParticleRenderType("rift", RIFT_PARTICLE);

    private ShaderHelper() {
    }

}
