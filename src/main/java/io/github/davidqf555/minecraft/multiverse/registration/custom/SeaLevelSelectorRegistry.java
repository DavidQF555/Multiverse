package io.github.davidqf555.minecraft.multiverse.registration.custom;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.SeaLevelSelector;
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
public final class SeaLevelSelectorRegistry {

    public static final ResourceKey<Registry<SeaLevelSelector>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "sea_level_selector"));
    private static Supplier<IForgeRegistry<SeaLevelSelector>> registry = null;

    private SeaLevelSelectorRegistry() {
    }

    public static IForgeRegistry<SeaLevelSelector> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<SeaLevelSelector>().setType(SeaLevelSelector.class).setName(LOCATION.location()).dataPackRegistry(SeaLevelSelector.DIRECT_CODEC));
    }

}
