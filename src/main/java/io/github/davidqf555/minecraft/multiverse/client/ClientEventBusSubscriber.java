package io.github.davidqf555.minecraft.multiverse.client;

import io.github.davidqf555.minecraft.multiverse.client.colors.KaleiditeCurrentColor;
import io.github.davidqf555.minecraft.multiverse.client.colors.KaleiditeTargetColor;
import io.github.davidqf555.minecraft.multiverse.client.particles.RiftExplosionParticle;
import io.github.davidqf555.minecraft.multiverse.client.particles.RiftExplosionSeedParticle;
import io.github.davidqf555.minecraft.multiverse.client.particles.RiftParticle;
import io.github.davidqf555.minecraft.multiverse.client.render.*;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.DimensionEffectsRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

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
        event.registerShader(ClientHelper.RIFT_SHADER);
        event.registerShader(ClientHelper.RIFT_PARTICLE_SHADER);
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
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(WarpShieldRenderer.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register(KaleiditeCurrentColor.Item.INSTANCE, ItemRegistry.KALEIDITE_CLUSTER.get());
        event.register(KaleiditeCurrentColor.Item.INSTANCE, ItemRegistry.KALEIDITE_PICKAXE.get());
        event.register(KaleiditeCurrentColor.Item.INSTANCE, ItemRegistry.KALEIDITE_SHOVEL.get());
        event.register(KaleiditeCurrentColor.Item.INSTANCE, ItemRegistry.KALEIDITE_AXE.get());
        event.register(KaleiditeCurrentColor.Item.INSTANCE, ItemRegistry.KALEIDITE_SWORD.get());
        event.register(KaleiditeTargetColor.INSTANCE, ItemRegistry.PRISMATIC_PICKAXE.get());
        event.register(KaleiditeTargetColor.INSTANCE, ItemRegistry.PRISMATIC_SHOVEL.get());
        event.register(KaleiditeTargetColor.INSTANCE, ItemRegistry.PRISMATIC_AXE.get());
        event.register(KaleiditeTargetColor.INSTANCE, ItemRegistry.PRISMATIC_SWORD.get());
        event.register(KaleiditeTargetColor.INSTANCE, ItemRegistry.WARP_STICK.get());
        event.register(KaleiditeTargetColor.INSTANCE, ItemRegistry.WARP_RING.get());
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleTypeRegistry.RIFT.get(), RiftParticle.Provider::new);
        event.registerSpriteSet(ParticleTypeRegistry.RIFT_EXPLOSION.get(), RiftExplosionParticle.Provider::new);
        event.registerSpecial(ParticleTypeRegistry.RIFT_EXPLOSION_EMITTER.get(), new RiftExplosionSeedParticle.Provider());
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return WarpShieldRenderer.INSTANCE;
            }
        }, ItemRegistry.WARP_SHIELD.get());
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ItemRegistry.BEACON_CROSSBOW.get(), ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "pull"), ItemProperties.getProperty(Items.CROSSBOW.getDefaultInstance(), ResourceLocation.withDefaultNamespace("pull")));
            ItemProperties.register(ItemRegistry.BEACON_CROSSBOW.get(), ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "pulling"), ItemProperties.getProperty(Items.CROSSBOW.getDefaultInstance(), ResourceLocation.withDefaultNamespace("pulling")));
            ItemProperties.register(ItemRegistry.BEACON_CROSSBOW.get(), ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "charged"), ItemProperties.getProperty(Items.CROSSBOW.getDefaultInstance(), ResourceLocation.withDefaultNamespace("charged")));
            ItemProperties.register(ItemRegistry.BEACON_CROSSBOW.get(), ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "firework"), ItemProperties.getProperty(Items.CROSSBOW.getDefaultInstance(), ResourceLocation.withDefaultNamespace("firework")));
            ItemProperties.register(ItemRegistry.WARP_SHIELD.get(), ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "blocking"), ItemProperties.getProperty(Items.SHIELD.getDefaultInstance(), ResourceLocation.withDefaultNamespace("blocking")));
        });
    }

}
