package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type.BiomeDimensionTypeProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class BiomeDimensionTypeProviderRegistry {

    public static final ResourceKey<Registry<BiomeDimensionTypeProvider>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimension_type_provider"));

    private BiomeDimensionTypeProviderRegistry() {
    }

    @SubscribeEvent
    public static void onNewDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LOCATION, BiomeDimensionTypeProvider.DIRECT_CODEC);
    }

}
