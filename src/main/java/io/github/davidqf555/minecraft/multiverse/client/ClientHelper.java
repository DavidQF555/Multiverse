package io.github.davidqf555.minecraft.multiverse.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.davidqf555.minecraft.multiverse.client.colors.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class ClientHelper {

    public static final ShaderProgram RIFT_SHADER = new ShaderProgram(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "core/rift"), DefaultVertexFormat.POSITION_COLOR, ShaderDefines.EMPTY);
    public static final ShaderProgram RIFT_PARTICLE_SHADER = new ShaderProgram(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "core/rift_particle"), DefaultVertexFormat.PARTICLE, ShaderDefines.EMPTY);
    public static final ResourceLocation RIFT = ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "textures/block/rift.png");
    @SuppressWarnings("deprecation")
    private static final RenderType RIFT_PARTICLE_TYPE = RenderType.create(
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
                            .add(ClientHelper.RIFT, false, false)
                            .build())
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderStateShard.PARTICLES_TARGET)
                    .createCompositeState(false)
    );
    public static final ParticleRenderType RIFT_PARTICLE = new ParticleRenderType("rift", RIFT_PARTICLE_TYPE);

    private ClientHelper() {
    }

    public static void addRiftParticles(Optional<ResourceKey<Level>> dim, Vec3 center) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            ResourceKey<Level> from = dim.orElseGet(() -> DimensionHelper.randomMultiverseDimension(world.getRandom(), Optional.of(world.dimension())));
            int color = MultiverseColorHelper.getColor(world, from);
            world.addParticle(ParticleTypeRegistry.RIFT.get(), center.x(), center.y(), center.z(), ARGB.red(color) / 255.0, ARGB.green(color) / 255.0, ARGB.blue(color) / 255.0);
        }
    }

    public static void addRiftExplosionParticles(Optional<ResourceKey<Level>> dim, Vec3 center) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            ResourceKey<Level> from = dim.orElseGet(() -> DimensionHelper.randomMultiverseDimension(world.getRandom(), Optional.of(world.dimension())));
            int color = MultiverseColorHelper.getColor(world, from);
            world.addParticle(ParticleTypeRegistry.RIFT_EXPLOSION_EMITTER.get(), center.x(), center.y(), center.z(), ARGB.red(color) / 255.0, ARGB.green(color) / 255.0, ARGB.blue(color) / 255.0);
        }
    }

}
