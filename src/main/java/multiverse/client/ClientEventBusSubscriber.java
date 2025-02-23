package multiverse.client;

import multiverse.client.colors.KaleiditeCurrentColor;
import multiverse.client.colors.KaleiditeTargetColor;
import multiverse.client.particles.RiftEffectParticle;
import multiverse.client.particles.RiftExplosionParticle;
import multiverse.client.particles.RiftExplosionSeedParticle;
import multiverse.client.particles.RiftParticle;
import multiverse.client.render.*;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.DimensionEffectsRegistry;
import multiverse.registration.BlockRegistry;
import multiverse.registration.EntityRegistry;
import multiverse.registration.ParticleTypeRegistry;
import multiverse.registration.TileEntityRegistry;
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
        event.registerSpriteSet(ParticleTypeRegistry.RIFT_EFFECT.get(), RiftEffectParticle.Provider::new);
        event.registerSpriteSet(ParticleTypeRegistry.RIFT_EXPLOSION.get(), RiftExplosionParticle.Provider::new);
        event.registerSpecial(ParticleTypeRegistry.RIFT_EXPLOSION_EMITTER.get(), new RiftExplosionSeedParticle.Provider());
    }

}
