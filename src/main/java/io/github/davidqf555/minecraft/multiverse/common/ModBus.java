package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.registration.ItemRegistry;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModBus {

    private ModBus() {
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> DispenserBlock.registerProjectileBehavior(ItemRegistry.KALEIDITE_CORE.get()));
    }

}
