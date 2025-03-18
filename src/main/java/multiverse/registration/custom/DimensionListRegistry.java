package multiverse.registration.custom;

import multiverse.common.Multiverse;
import multiverse.common.world.DimensionList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class DimensionListRegistry {

    public static final ResourceKey<Registry<DimensionList>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimension_lists"));
    public static final ResourceKey<DimensionList> TARGETS = ResourceKey.create(LOCATION, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "targets"));

    private DimensionListRegistry() {
    }

    @SubscribeEvent
    public static void onNewDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LOCATION, DimensionList.DIRECT_CODEC);
    }

}
