package multiverse.registration.custom.biomes;

import com.mojang.serialization.MapCodec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsGenerator;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings.TypeMapNoiseGeneratorSettingsGenerator;
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
public final class BiomeNoiseGeneratorSettingsGeneratorTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends BiomeNoiseGeneratorSettingsGenerator>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "noise_settings_generator_type"));
    public static final DeferredRegister<MapCodec<? extends BiomeNoiseGeneratorSettingsGenerator>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeNoiseGeneratorSettingsGenerator>, MapCodec<TypeMapNoiseGeneratorSettingsGenerator>> TYPE_MAP = register("type_map", () -> TypeMapNoiseGeneratorSettingsGenerator.CODEC);
    private static Registry<MapCodec<? extends BiomeNoiseGeneratorSettingsGenerator>> registry = null;

    private BiomeNoiseGeneratorSettingsGeneratorTypeRegistry() {
    }

    private static <T extends BiomeNoiseGeneratorSettingsGenerator> DeferredHolder<MapCodec<? extends BiomeNoiseGeneratorSettingsGenerator>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends BiomeNoiseGeneratorSettingsGenerator>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}
