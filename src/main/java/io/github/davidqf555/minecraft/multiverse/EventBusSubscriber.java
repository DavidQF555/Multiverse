package io.github.davidqf555.minecraft.multiverse;

import io.github.davidqf555.minecraft.multiverse.packets.UpdateClientDimensionsPacket;
import io.github.davidqf555.minecraft.multiverse.world.MultiverseChunkGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EventBusSubscriber {

    private static int index = 0;

    private EventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            UpdateClientDimensionsPacket.register(index++);
            Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Multiverse.MOD_ID, "multiverse_chunk_generator_codec"), MultiverseChunkGenerator.CODEC);
        });

    }
}
