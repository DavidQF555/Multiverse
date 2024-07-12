package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.FlatSeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.SeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.WaveSeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.WeightedSeaLevelSelector;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class SeaLevelSelectorRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends SeaLevelSelector>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "sea_level_selector"));
    public static final DeferredRegister<MapCodec<? extends SeaLevelSelector>> CODECS = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends SeaLevelSelector>, MapCodec<FlatSeaLevelSelector>> FLAT = register("flat", () -> FlatSeaLevelSelector.CODEC);
    public static final DeferredHolder<MapCodec<? extends SeaLevelSelector>, MapCodec<WaveSeaLevelSelector>> WAVE = register("wave", () -> WaveSeaLevelSelector.CODEC);
    public static final DeferredHolder<MapCodec<? extends SeaLevelSelector>, MapCodec<WeightedSeaLevelSelector>> WEIGHTED = register("weighted", WeightedSeaLevelSelector.CODEC);

    private static Registry<MapCodec<? extends SeaLevelSelector>> registry;

    private SeaLevelSelectorRegistry() {
    }

    public static <T extends SeaLevelSelector> DeferredHolder<MapCodec<? extends SeaLevelSelector>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return CODECS.register(name, codec);
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

    @Nullable
    public static Registry<MapCodec<? extends SeaLevelSelector>> getRegistry() {
        return registry;
    }

}

