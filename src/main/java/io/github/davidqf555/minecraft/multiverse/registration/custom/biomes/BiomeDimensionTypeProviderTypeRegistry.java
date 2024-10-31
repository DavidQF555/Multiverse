package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type.BiomeDimensionTypeProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type.TypeMapDimensionTypeProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type.WeightedDimensionTypeProvider;
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
public final class BiomeDimensionTypeProviderTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends BiomeDimensionTypeProvider>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimension_type_provider_type"));
    public static final DeferredRegister<MapCodec<? extends BiomeDimensionTypeProvider>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeDimensionTypeProvider>, MapCodec<WeightedDimensionTypeProvider>> WEIGHTED = register("weighted", () -> WeightedDimensionTypeProvider.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeDimensionTypeProvider>, MapCodec<TypeMapDimensionTypeProvider>> TYPE_MAP = register("type_map", () -> TypeMapDimensionTypeProvider.CODEC);
    private static Registry<MapCodec<? extends BiomeDimensionTypeProvider>> registry = null;

    private BiomeDimensionTypeProviderTypeRegistry() {
    }

    private static <T extends BiomeDimensionTypeProvider> DeferredHolder<MapCodec<? extends BiomeDimensionTypeProvider>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends BiomeDimensionTypeProvider>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}
