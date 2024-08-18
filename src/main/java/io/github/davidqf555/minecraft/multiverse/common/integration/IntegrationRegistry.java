package io.github.davidqf555.minecraft.multiverse.common.integration;

import io.github.davidqf555.minecraft.multiverse.common.ConfigHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.Registry;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class IntegrationRegistry {

    private IntegrationRegistry() {
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        if (ModList.get().isLoaded("terrablender")) {
            ConfigHelper.biomes = new TerraBlenderBiomes(event.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));
        }
    }

}
