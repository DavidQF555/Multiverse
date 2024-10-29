package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.biome_source.BiomeSourceProvider;
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

    public static final ResourceKey<Registry<Codec<? extends BiomeSourceProvider<?>>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "biome_source_provider"));
    public static final DeferredRegister<Codec<? extends BiomeSourceProvider<?>>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<NoiseBiomeSourceProvider>> NOISE = register("noise", NoiseBiomeSourceProvider.CODEC);
    private static Supplier<IForgeRegistry<Codec<? extends BiomeSourceProvider<?>>>> registry = null;

    private BiomeSourceProviderTypeRegistry() {
    }

    private static <T extends BiomeSourceProvider<?>> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static IForgeRegistry<Codec<? extends BiomeSourceProvider<?>>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Codec<? extends BiomeSourceProvider<?>>>().setName(LOCATION.location()));
    }

}
