package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.dim_type.BiomeDimensionTypeGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.dim_type.TypeMapDimensionTypeGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.dim_type.WeightedDimensionTypeGenerator;
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
public final class BiomeDimensionTypeGeneratorTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends BiomeDimensionTypeGenerator>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimension_type_generator_type"));
    public static final DeferredRegister<MapCodec<? extends BiomeDimensionTypeGenerator>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeDimensionTypeGenerator>, MapCodec<WeightedDimensionTypeGenerator>> WEIGHTED = register("weighted", () -> WeightedDimensionTypeGenerator.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeDimensionTypeGenerator>, MapCodec<TypeMapDimensionTypeGenerator>> TYPE_MAP = register("type_map", () -> TypeMapDimensionTypeGenerator.CODEC);
    private static Registry<MapCodec<? extends BiomeDimensionTypeGenerator>> registry = null;

    private BiomeDimensionTypeGeneratorTypeRegistry() {
    }

    private static <T extends BiomeDimensionTypeGenerator> DeferredHolder<MapCodec<? extends BiomeDimensionTypeGenerator>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends BiomeDimensionTypeGenerator>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}
