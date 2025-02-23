package multiverse.registration.custom;

import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.SeaLevelGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class SeaLevelGeneratorRegistry {

    public static final ResourceKey<Registry<SeaLevelGenerator>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "sea_level_generator"));

    private SeaLevelGeneratorRegistry() {
    }

    @SubscribeEvent
    public static void onNewDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LOCATION, SeaLevelGenerator.DIRECT_CODEC);
    }

}
