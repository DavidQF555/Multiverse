package io.github.davidqf555.minecraft.multiverse.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import io.github.davidqf555.minecraft.multiverse.client.effects.ColoredFogEffect;
import io.github.davidqf555.minecraft.multiverse.client.render.MixedIllagerRenderer;
import io.github.davidqf555.minecraft.multiverse.client.render.RiftTileEntityRenderer;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.entities.CollectorEntity;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.TileEntityRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.DimensionTypeEffectsRegistry;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
        event.registerEntityRenderer(EntityRegistry.COLLECTOR.get(), MixedIllagerRenderer<CollectorEntity>::new);
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Multiverse.MOD_ID, "rift"), DefaultVertexFormat.POSITION_COLOR), shader -> RiftTileEntityRenderer.SHADER = shader);
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            DimensionTypeEffectsRegistry.FOG.forEach((key, color) -> DimensionSpecialEffects.EFFECTS.put(key, new ColoredFogEffect(color)));
        });
    }

}
