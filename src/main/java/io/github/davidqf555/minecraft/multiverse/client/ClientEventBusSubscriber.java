package io.github.davidqf555.minecraft.multiverse.client;

import io.github.davidqf555.minecraft.multiverse.client.colors.KaleiditeCurrentColor;
import io.github.davidqf555.minecraft.multiverse.client.colors.KaleiditeTargetColor;
import io.github.davidqf555.minecraft.multiverse.client.particles.RiftExplosionParticle;
import io.github.davidqf555.minecraft.multiverse.client.particles.RiftExplosionSeedParticle;
import io.github.davidqf555.minecraft.multiverse.client.particles.RiftParticle;
import io.github.davidqf555.minecraft.multiverse.client.render.*;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.DimensionEffectsRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.TileEntityRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEventBusSubscriber {

    private ClientEventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TileEntityRegistry.RIFT.get(), dispatcher -> new RiftTileEntityRenderer());
        event.registerEntityRenderer(EntityRegistry.TRAVELER.get(), TravelerRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DOPPELGANGER.get(), DoppelgangerRenderer::new);
        event.registerEntityRenderer(EntityRegistry.KALEIDITE_CORE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(EntityRegistry.CONQUEROR.get(), ConquerorRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        event.registerShader(ShaderHelper.RIFT_SHADER);
        event.registerShader(ShaderHelper.RIFT_PARTICLE_SHADER);
    }

    @SubscribeEvent
    public static void onRegisterDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        DimensionEffectsRegistry.FOG.forEach((loc, color) -> event.register(loc, new ColoredFogEffect(color)));
    }

    @SubscribeEvent
    public static void onRegisterBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register(KaleiditeCurrentColor.Block.INSTANCE, BlockRegistry.KALEIDITE_CLUSTER.get());
    }

    @SubscribeEvent
    public static void onRegisterSpecialModelRenderer(RegisterSpecialModelRendererEvent event) {
        event.register(WarpShieldRenderer.LOCATION, WarpShieldRenderer.Unbaked.CODEC);
    }

    @SubscribeEvent
    public static void onRegisterItemColorHandlers(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(KaleiditeTargetColor.LOCATION, KaleiditeTargetColor.CODEC);
        event.register(KaleiditeCurrentColor.Item.LOCATION, KaleiditeCurrentColor.Item.CODEC);
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleTypeRegistry.RIFT.get(), RiftParticle.Provider::new);
        event.registerSpriteSet(ParticleTypeRegistry.RIFT_EXPLOSION.get(), RiftExplosionParticle.Provider::new);
        event.registerSpecial(ParticleTypeRegistry.RIFT_EXPLOSION_EMITTER.get(), new RiftExplosionSeedParticle.Provider());
    }

}
