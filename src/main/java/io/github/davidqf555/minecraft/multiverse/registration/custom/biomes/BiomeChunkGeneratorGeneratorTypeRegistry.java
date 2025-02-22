package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.BiomeChunkGeneratorGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.BiomeChunkGeneratorGeneratorType;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.NoiseChunkGeneratorGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeChunkGeneratorGeneratorTypeRegistry {

    public static final ResourceKey<Registry<BiomeChunkGeneratorGeneratorType<?>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "biome_chunk_generator"));
    public static final DeferredRegister<BiomeChunkGeneratorGeneratorType<?>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<BiomeChunkGeneratorGeneratorType<NoiseChunkGeneratorGenerator>> NOISE = register("noise", () -> NoiseChunkGeneratorGenerator.CODEC);
    private static Supplier<IForgeRegistry<BiomeChunkGeneratorGeneratorType<?>>> registry = null;

    private BiomeChunkGeneratorGeneratorTypeRegistry() {
    }

    private static <T extends BiomeChunkGeneratorGenerator<?>> RegistryObject<BiomeChunkGeneratorGeneratorType<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new BiomeChunkGeneratorGeneratorType<>(codec.get()));
    }

    public static IForgeRegistry<BiomeChunkGeneratorGeneratorType<?>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<BiomeChunkGeneratorGeneratorType<?>>().setName(LOCATION.location()).setType((Class<BiomeChunkGeneratorGeneratorType<?>>) (Class<?>) BiomeChunkGeneratorGeneratorType.class));
    }

}
