package multiverse.registration.custom.generator.biomes;

import com.mojang.serialization.MapCodec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.BiomeDimensionGenerator;
import multiverse.common.world.worldgen.generators.biomes.DualBiomeDimensionGenerator;
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
public final class BiomeDimensionGeneratorTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends BiomeDimensionGenerator>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "generator/biome_dimensions"));
    public static final DeferredRegister<MapCodec<? extends BiomeDimensionGenerator>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeDimensionGenerator>, MapCodec<DualBiomeDimensionGenerator>> DUAL = register("dual", () -> DualBiomeDimensionGenerator.CODEC);
    private static Registry<MapCodec<? extends BiomeDimensionGenerator>> registry = null;

    private BiomeDimensionGeneratorTypeRegistry() {
    }

    private static <T extends BiomeDimensionGenerator> DeferredHolder<MapCodec<? extends BiomeDimensionGenerator>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends BiomeDimensionGenerator>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}
