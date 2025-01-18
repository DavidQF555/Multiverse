package io.github.davidqf555.minecraft.multiverse.registration.custom;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.DimensionProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.BiomeConfigDimensionProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class DimensionProviderTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends DimensionProvider>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimension_provider"));
    public static final DeferredRegister<MapCodec<? extends DimensionProvider>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends DimensionProvider>, MapCodec<BiomeConfigDimensionProvider>> BIOME_CONFIG = register("biome_config", () -> BiomeConfigDimensionProvider.CODEC);
    private static Registry<MapCodec<? extends DimensionProvider>> registry = null;

    private DimensionProviderTypeRegistry() {
    }

    private static <T extends DimensionProvider> DeferredHolder<MapCodec<? extends DimensionProvider>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends DimensionProvider>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}