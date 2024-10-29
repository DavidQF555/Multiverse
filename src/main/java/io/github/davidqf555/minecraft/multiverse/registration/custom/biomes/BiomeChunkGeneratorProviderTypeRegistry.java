package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.BiomeChunkGeneratorProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.NoiseChunkGeneratorProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeChunkGeneratorProviderTypeRegistry {

    public static final ResourceKey<Registry<Codec<? extends BiomeChunkGeneratorProvider<?>>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "biome_chunk_provider"));
    public static final DeferredRegister<Codec<? extends BiomeChunkGeneratorProvider<?>>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<NoiseChunkGeneratorProvider>> NOISE = register("noise", () -> NoiseChunkGeneratorProvider.CODEC);
    private static Supplier<IForgeRegistry<Codec<? extends BiomeChunkGeneratorProvider<?>>>> registry = null;

    private BiomeChunkGeneratorProviderTypeRegistry() {
    }

    private static <T extends BiomeChunkGeneratorProvider<?>> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static IForgeRegistry<Codec<? extends BiomeChunkGeneratorProvider<?>>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Codec<? extends BiomeChunkGeneratorProvider<?>>>().setName(LOCATION.location()));
    }

}
