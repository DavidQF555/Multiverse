package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.BiomeChunkGeneratorProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.NoiseChunkGeneratorProvider;
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
public final class BiomeChunkGeneratorProviderTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends BiomeChunkGeneratorProvider<?>>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "biome_chunk_provider"));
    public static final DeferredRegister<MapCodec<? extends BiomeChunkGeneratorProvider<?>>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeChunkGeneratorProvider<?>>, MapCodec<NoiseChunkGeneratorProvider>> NOISE = register("noise", () -> NoiseChunkGeneratorProvider.CODEC);
    private static Registry<MapCodec<? extends BiomeChunkGeneratorProvider<?>>> registry = null;

    private BiomeChunkGeneratorProviderTypeRegistry() {
    }

    private static <T extends BiomeChunkGeneratorProvider<?>> DeferredHolder<MapCodec<? extends BiomeChunkGeneratorProvider<?>>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends BiomeChunkGeneratorProvider<?>>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}
