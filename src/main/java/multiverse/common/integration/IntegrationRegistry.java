package multiverse.common.integration;

import multiverse.common.Multiverse;
import multiverse.common.util.MultiverseConfig;
import multiverse.common.world.worldgen.biomes.LazyMultiverseBiomes;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class IntegrationRegistry {

    private IntegrationRegistry() {
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        if (ModList.get().isLoaded("terrablender")) {
            MultiverseConfig.setBiomesManager(new LazyMultiverseBiomes(() -> new TerraBlenderBiomes(event.getServer().registryAccess().registryOrThrow(Registries.BIOME))));
        }
    }

}
