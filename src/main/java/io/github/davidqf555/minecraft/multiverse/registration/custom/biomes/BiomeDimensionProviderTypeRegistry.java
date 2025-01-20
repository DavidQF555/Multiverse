package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.BiomeDimensionProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.DualBiomeDimensionProvider;
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
public final class BiomeDimensionProviderTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends BiomeDimensionProvider>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "biome_dimension_provider"));
    public static final DeferredRegister<MapCodec<? extends BiomeDimensionProvider>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeDimensionProvider>, MapCodec<DualBiomeDimensionProvider>> DUAL = register("dual", () -> DualBiomeDimensionProvider.CODEC);
    private static Registry<MapCodec<? extends BiomeDimensionProvider>> registry = null;

    private BiomeDimensionProviderTypeRegistry() {
    }

    private static <T extends BiomeDimensionProvider> DeferredHolder<MapCodec<? extends BiomeDimensionProvider>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends BiomeDimensionProvider>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}
