package multiverse.registration.custom;

import multiverse.common.Multiverse;
import multiverse.common.world.DimensionList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DimensionListRegistry {

    public static final ResourceKey<Registry<DimensionList>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "dimension_lists"));
    public static final ResourceKey<DimensionList> TARGETS = ResourceKey.create(LOCATION, new ResourceLocation(Multiverse.MOD_ID, "targets"));
    private static Supplier<IForgeRegistry<DimensionList>> registry = null;

    private DimensionListRegistry() {
    }

    public static IForgeRegistry<DimensionList> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<DimensionList>().setType(DimensionList.class).setName(LOCATION.location()).dataPackRegistry(DimensionList.DIRECT_CODEC));
    }

}
