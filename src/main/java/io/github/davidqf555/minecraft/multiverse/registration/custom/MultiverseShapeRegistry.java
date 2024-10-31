package io.github.davidqf555.minecraft.multiverse.registration.custom;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class MultiverseShapeRegistry {

    public static final ResourceKey<Registry<MultiverseShape>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiverse_shape"));

    private MultiverseShapeRegistry() {
    }

    @SubscribeEvent
    public static void onNewDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LOCATION, MultiverseShape.DIRECT_CODEC);
    }


}
