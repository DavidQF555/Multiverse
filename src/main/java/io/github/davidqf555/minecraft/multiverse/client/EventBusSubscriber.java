package io.github.davidqf555.minecraft.multiverse.client;

import io.github.davidqf555.minecraft.multiverse.client.render.MixedIllagerRenderer;
import io.github.davidqf555.minecraft.multiverse.client.render.RiftTileEntityRenderer;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.RegistryHandler;
import io.github.davidqf555.minecraft.multiverse.common.entities.DimensionBossEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EventBusSubscriber {

    private EventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(RegistryHandler.RIFT_TILE_ENTITY_TYPE.get(), RiftTileEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.DIMENSION_BOSS_ENTITY.get(), MixedIllagerRenderer<DimensionBossEntity>::new);
    }

}
