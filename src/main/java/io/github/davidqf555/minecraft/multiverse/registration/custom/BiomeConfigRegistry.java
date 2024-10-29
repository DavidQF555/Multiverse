package io.github.davidqf555.minecraft.multiverse.registration.custom;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.BiomeConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeConfigRegistry {

    public static final ResourceKey<Registry<BiomeConfig>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "biome_config"));
    public static final ResourceKey<BiomeConfig> NORMAL = ResourceKey.create(LOCATION, new ResourceLocation(Multiverse.MOD_ID, "normal"));
    public static final ResourceKey<BiomeConfig> ROOFED = ResourceKey.create(LOCATION, new ResourceLocation(Multiverse.MOD_ID, "roofed"));
    public static final ResourceKey<BiomeConfig> ISLANDS = ResourceKey.create(LOCATION, new ResourceLocation(Multiverse.MOD_ID, "islands"));
    private static Supplier<IForgeRegistry<BiomeConfig>> registry = null;

    private BiomeConfigRegistry() {
    }

    public static IForgeRegistry<BiomeConfig> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<BiomeConfig>().setName(LOCATION.location()));
    }

    @SubscribeEvent
    public static void onNewDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LOCATION, BiomeConfig.DIRECT_CODEC);
    }


}
