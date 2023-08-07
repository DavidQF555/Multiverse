package io.github.davidqf555.minecraft.multiverse.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import io.github.davidqf555.minecraft.multiverse.client.effects.ColoredFogEffect;
import io.github.davidqf555.minecraft.multiverse.client.render.*;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.entities.CollectorEntity;
import io.github.davidqf555.minecraft.multiverse.registration.*;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.DimensionTypeEffectsRegistry;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EventBusSubscriber {

    private EventBusSubscriber() {
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
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Multiverse.MOD_ID, "rift"), DefaultVertexFormat.POSITION_COLOR), shader -> RiftTileEntityRenderer.SHADER = shader);
    }

    @SubscribeEvent
    public static void onRegisterDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        DimensionTypeEffectsRegistry.FOG.forEach((key, color) -> event.register(key, new ColoredFogEffect(color)));
    }

    @SubscribeEvent
    public static void onRegisterBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.getBlockColors().register(KaleiditeColor.Block.INSTANCE, BlockRegistry.KALEIDITE_CLUSTER.get());
    }

    @SubscribeEvent
    public static void onRegisterBlockColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.getItemColors().register(KaleiditeColor.Item.INSTANCE, ItemRegistry.KALEIDITE_CLUSTER.get());
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleTypeRegistry.RIFT.get(), RiftParticle.Provider::new);
    }

}
