package io.github.davidqf555.minecraft.multiverse.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import io.github.davidqf555.minecraft.multiverse.client.effects.ColoredFogEffect;
import io.github.davidqf555.minecraft.multiverse.client.render.*;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.entities.CollectorEntity;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.effects.DimensionEffectsRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.*;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

import java.io.IOException;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEventBusSubscriber {

    private ClientEventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TileEntityRegistry.RIFT.get(), dispatcher -> new RiftTileEntityRenderer());
        event.registerEntityRenderer(EntityRegistry.COLLECTOR.get(), CollectorRenderer<CollectorEntity>::new);
        event.registerEntityRenderer(EntityRegistry.TRAVELER.get(), TravelerRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DOPPELGANGER.get(), DoppelgangerRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "rift"), DefaultVertexFormat.POSITION_COLOR), shader -> ClientHelper.riftShader = shader);
    }

    @SubscribeEvent
    public static void onRegisterDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        DimensionEffectsRegistry.FOG.forEach((color, loc) -> event.register(loc, new ColoredFogEffect(color.getFireworkColor())));
    }

    @SubscribeEvent
    public static void onRegisterBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register(KaleiditeCurrentColor.Block.INSTANCE, BlockRegistry.KALEIDITE_CLUSTER.get());
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
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleTypeRegistry.RIFT.get(), RiftParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ItemRegistry.KALEIDITE_CROSSBOW.get(), ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "pull"), ItemProperties.getProperty(Items.CROSSBOW.getDefaultInstance(), ResourceLocation.withDefaultNamespace("pull")));
            ItemProperties.register(ItemRegistry.KALEIDITE_CROSSBOW.get(), ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "pulling"), ItemProperties.getProperty(Items.CROSSBOW.getDefaultInstance(), ResourceLocation.withDefaultNamespace("pulling")));
            ItemProperties.register(ItemRegistry.KALEIDITE_CROSSBOW.get(), ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "charged"), ItemProperties.getProperty(Items.CROSSBOW.getDefaultInstance(), ResourceLocation.withDefaultNamespace("charged")));
            ItemProperties.register(ItemRegistry.KALEIDITE_CROSSBOW.get(), ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "firework"), ItemProperties.getProperty(Items.CROSSBOW.getDefaultInstance(), ResourceLocation.withDefaultNamespace("firework")));
        });
    }

}
