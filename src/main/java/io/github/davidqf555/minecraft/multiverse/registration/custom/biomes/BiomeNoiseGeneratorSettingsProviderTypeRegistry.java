package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.noise_settings.TypeMapNoiseGeneratorSettingsProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeNoiseGeneratorSettingsProviderTypeRegistry {

    public static final ResourceKey<Registry<Codec<? extends BiomeNoiseGeneratorSettingsProvider>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "noise_generator_settings_type"));
    public static final DeferredRegister<Codec<? extends BiomeNoiseGeneratorSettingsProvider>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<TypeMapNoiseGeneratorSettingsProvider>> TYPE_MAP = register("type_map", () -> TypeMapNoiseGeneratorSettingsProvider.CODEC);
    private static Supplier<IForgeRegistry<Codec<? extends BiomeNoiseGeneratorSettingsProvider>>> registry = null;

    private BiomeNoiseGeneratorSettingsProviderTypeRegistry() {
    }

    private static <T extends BiomeNoiseGeneratorSettingsProvider> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static IForgeRegistry<Codec<? extends BiomeNoiseGeneratorSettingsProvider>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Codec<? extends BiomeNoiseGeneratorSettingsProvider>>().setName(LOCATION.location()));
    }

}
