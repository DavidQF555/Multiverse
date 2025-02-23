package multiverse.registration.custom.biomes;

import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.dim_type.BiomeDimensionTypeGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class BiomeDimensionTypeGeneratorRegistry {

    public static final ResourceKey<Registry<BiomeDimensionTypeGenerator>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimension_type_generator"));

    private BiomeDimensionTypeGeneratorRegistry() {
    }

    @SubscribeEvent
    public static void onNewDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LOCATION, BiomeDimensionTypeGenerator.DIRECT_CODEC);
    }

}
