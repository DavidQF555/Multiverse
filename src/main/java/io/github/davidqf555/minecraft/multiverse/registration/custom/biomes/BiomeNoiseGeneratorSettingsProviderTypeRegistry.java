package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.noise_settings.TypeMapNoiseGeneratorSettingsProvider;
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
public final class BiomeNoiseGeneratorSettingsProviderTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends BiomeNoiseGeneratorSettingsProvider>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "noise_generator_settings_type"));
    public static final DeferredRegister<MapCodec<? extends BiomeNoiseGeneratorSettingsProvider>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeNoiseGeneratorSettingsProvider>, MapCodec<TypeMapNoiseGeneratorSettingsProvider>> TYPE_MAP = register("type_map", () -> TypeMapNoiseGeneratorSettingsProvider.CODEC);
    private static Registry<MapCodec<? extends BiomeNoiseGeneratorSettingsProvider>> registry = null;

    private BiomeNoiseGeneratorSettingsProviderTypeRegistry() {
    }

    private static <T extends BiomeNoiseGeneratorSettingsProvider> DeferredHolder<MapCodec<? extends BiomeNoiseGeneratorSettingsProvider>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends BiomeNoiseGeneratorSettingsProvider>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}
