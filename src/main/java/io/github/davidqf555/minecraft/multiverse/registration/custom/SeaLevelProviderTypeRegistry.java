package io.github.davidqf555.minecraft.multiverse.registration.custom;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.sea_level.FlatSeaLevelProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.sea_level.SeaLevelProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.sea_level.WaveSeaLevelProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.sea_level.WeightedSeaLevelProvider;
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
public final class SeaLevelProviderTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends SeaLevelProvider>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "sea_level_provider_type"));
    public static final DeferredRegister<MapCodec<? extends SeaLevelProvider>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends SeaLevelProvider>, MapCodec<FlatSeaLevelProvider>> FLAT = register("flat", () -> FlatSeaLevelProvider.CODEC);
    public static final DeferredHolder<MapCodec<? extends SeaLevelProvider>, MapCodec<WaveSeaLevelProvider>> WAVE = register("wave", () -> WaveSeaLevelProvider.CODEC);
    public static final DeferredHolder<MapCodec<? extends SeaLevelProvider>, MapCodec<WeightedSeaLevelProvider>> WEIGHTED = register("weighted", () -> WeightedSeaLevelProvider.CODEC);
    private static Registry<MapCodec<? extends SeaLevelProvider>> registry = null;

    private SeaLevelProviderTypeRegistry() {
    }

    private static <T extends SeaLevelProvider> DeferredHolder<MapCodec<? extends SeaLevelProvider>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends SeaLevelProvider>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}
