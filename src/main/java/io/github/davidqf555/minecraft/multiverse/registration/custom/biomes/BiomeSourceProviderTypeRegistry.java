package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.biome_source.BiomeSourceProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.biome_source.NoiseBiomeSourceProvider;
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
public final class BiomeSourceProviderTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends BiomeSourceProvider<?>>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "biome_source_provider"));
    public static final DeferredRegister<MapCodec<? extends BiomeSourceProvider<?>>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeSourceProvider<?>>, MapCodec<NoiseBiomeSourceProvider>> NOISE = register("noise", () -> NoiseBiomeSourceProvider.CODEC);
    private static Registry<MapCodec<? extends BiomeSourceProvider<?>>> registry = null;

    private BiomeSourceProviderTypeRegistry() {
    }

    private static <T extends BiomeSourceProvider<?>> DeferredHolder<MapCodec<? extends BiomeSourceProvider<?>>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends BiomeSourceProvider<?>>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}
