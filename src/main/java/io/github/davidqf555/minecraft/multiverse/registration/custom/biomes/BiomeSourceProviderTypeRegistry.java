package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.biome_source.BiomeSourceProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.biome_source.BiomeSourceProviderType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.biome_source.NoiseBiomeSourceProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeSourceProviderTypeRegistry {

    public static ResourceKey<Registry<BiomeSourceProviderType<?>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "biome_source_provider"));
    public static final DeferredRegister<BiomeSourceProviderType<?>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<BiomeSourceProviderType<NoiseBiomeSourceProvider>> NOISE = register("noise", NoiseBiomeSourceProvider.CODEC);
    private static Supplier<IForgeRegistry<BiomeSourceProviderType<?>>> registry = null;

    private BiomeSourceProviderTypeRegistry() {
    }

    private static <T extends BiomeSourceProvider<?>> RegistryObject<BiomeSourceProviderType<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new BiomeSourceProviderType<>(codec.get()));
    }

    public static IForgeRegistry<BiomeSourceProviderType<?>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<BiomeSourceProviderType<?>>().setName(LOCATION.location()).setType((Class<BiomeSourceProviderType<?>>) (Class<?>) BiomeSourceProviderType.class));
    }

}
