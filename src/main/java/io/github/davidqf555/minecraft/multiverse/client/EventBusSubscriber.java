package io.github.davidqf555.minecraft.multiverse.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import io.github.davidqf555.minecraft.multiverse.client.effects.ColoredFogEffect;
import io.github.davidqf555.minecraft.multiverse.client.render.*;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.entities.CollectorEntity;
import io.github.davidqf555.minecraft.multiverse.registration.*;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.DimensionTypeEffectsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

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
        event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(Multiverse.MOD_ID, "rift"), DefaultVertexFormat.POSITION_COLOR), shader -> RiftTileEntityRenderer.SHADER = shader);
    }

    @SubscribeEvent
    public static void onHandleBlockColor(ColorHandlerEvent.Block event) {
        event.getBlockColors().register(KaleiditeColor.Block.INSTANCE, BlockRegistry.KALEIDITE_CLUSTER.get());
    }

    @SubscribeEvent
    public static void onHandleItemColor(ColorHandlerEvent.Item event) {
        event.getItemColors().register(KaleiditeColor.Item.INSTANCE, ItemRegistry.KALEIDITE_CLUSTER.get());
    }

    @SubscribeEvent
    public static void onRegisterParticleFactory(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleTypeRegistry.RIFT.get(), RiftParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            DimensionTypeEffectsRegistry.FOG.forEach((key, color) -> DimensionSpecialEffects.EFFECTS.put(key, new ColoredFogEffect(color)));
            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.KALEIDITE_CLUSTER.get(), RenderType.cutout());
            ItemProperties.register(ItemRegistry.KALEIDITE_CROSSBOW.get(), new ResourceLocation(Multiverse.MOD_ID, "pull"), ItemProperties.getProperty(Items.CROSSBOW, new ResourceLocation("pull")));
            ItemProperties.register(ItemRegistry.KALEIDITE_CROSSBOW.get(), new ResourceLocation(Multiverse.MOD_ID, "pulling"), ItemProperties.getProperty(Items.CROSSBOW, new ResourceLocation("pulling")));
            ItemProperties.register(ItemRegistry.KALEIDITE_CROSSBOW.get(), new ResourceLocation(Multiverse.MOD_ID, "charged"), ItemProperties.getProperty(Items.CROSSBOW, new ResourceLocation("charged")));
            ItemProperties.register(ItemRegistry.KALEIDITE_CROSSBOW.get(), new ResourceLocation(Multiverse.MOD_ID, "firework"), ItemProperties.getProperty(Items.CROSSBOW, new ResourceLocation("firework")));
        });
    }

}
