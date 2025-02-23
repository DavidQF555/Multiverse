package io.github.davidqf555.minecraft.multiverse.registration.custom;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.FlatSeaLevelGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.SeaLevelGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.WaveSeaLevelGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.WeightedSeaLevelGenerator;
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
public final class SeaLevelGeneratorTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends SeaLevelGenerator>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "sea_level_generator_type"));
    public static final DeferredRegister<MapCodec<? extends SeaLevelGenerator>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends SeaLevelGenerator>, MapCodec<FlatSeaLevelGenerator>> FLAT = register("flat", () -> FlatSeaLevelGenerator.CODEC);
    public static final DeferredHolder<MapCodec<? extends SeaLevelGenerator>, MapCodec<WaveSeaLevelGenerator>> WAVE = register("wave", () -> WaveSeaLevelGenerator.CODEC);
    public static final DeferredHolder<MapCodec<? extends SeaLevelGenerator>, MapCodec<WeightedSeaLevelGenerator>> WEIGHTED = register("weighted", () -> WeightedSeaLevelGenerator.CODEC);
    private static Registry<MapCodec<? extends SeaLevelGenerator>> registry = null;

    private SeaLevelGeneratorTypeRegistry() {
    }

    private static <T extends SeaLevelGenerator> DeferredHolder<MapCodec<? extends SeaLevelGenerator>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends SeaLevelGenerator>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}
