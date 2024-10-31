package io.github.davidqf555.minecraft.multiverse.common.integration;

import io.github.davidqf555.minecraft.multiverse.common.ConfigHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class IntegrationRegistry {

    private IntegrationRegistry() {
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        if (ModList.get().isLoaded("terrablender")) {
            ConfigHelper.biomes = new TerraBlenderBiomes(event.getServer().registryAccess().registryOrThrow(Registries.BIOME));
        }
    }

}
