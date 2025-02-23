package multiverse.common.integration;

import multiverse.common.Multiverse;
import multiverse.common.util.MultiverseConfig;
import multiverse.common.world.worldgen.biomes.LazyMultiverseBiomes;
import net.minecraft.core.Registry;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class IntegrationRegistry {

    private IntegrationRegistry() {
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        if (ModList.get().isLoaded("terrablender")) {
            MultiverseConfig.setBiomesManager(new LazyMultiverseBiomes(() -> new TerraBlenderBiomes(event.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY))));
        }
    }

}
