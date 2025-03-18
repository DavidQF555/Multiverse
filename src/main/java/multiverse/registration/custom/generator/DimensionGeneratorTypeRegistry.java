package multiverse.registration.custom.generator;

import com.mojang.serialization.MapCodec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.DimensionGenerator;
import multiverse.common.world.worldgen.generators.biomes.BiomeConfigDimensionGenerator;
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
public final class DimensionGeneratorTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends DimensionGenerator>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "generator/dimensions"));
    public static final DeferredRegister<MapCodec<? extends DimensionGenerator>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends DimensionGenerator>, MapCodec<BiomeConfigDimensionGenerator>> BIOME_CONFIG = register("biome_config", () -> BiomeConfigDimensionGenerator.CODEC);
    private static Registry<MapCodec<? extends DimensionGenerator>> registry = null;

    private DimensionGeneratorTypeRegistry() {
    }

    private static <T extends DimensionGenerator> DeferredHolder<MapCodec<? extends DimensionGenerator>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends DimensionGenerator>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}