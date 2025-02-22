package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source.BiomeSourceGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source.BiomeSourceGeneratorType;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source.NoiseBiomeSourceGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeSourceGeneratorTypeRegistry {

    public static final ResourceKey<Registry<BiomeSourceGeneratorType<?>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "biome_source_generator"));
    public static final DeferredRegister<BiomeSourceGeneratorType<?>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<BiomeSourceGeneratorType<NoiseBiomeSourceGenerator>> NOISE = register("noise", NoiseBiomeSourceGenerator.CODEC);
    private static Supplier<IForgeRegistry<BiomeSourceGeneratorType<?>>> registry = null;

    private BiomeSourceGeneratorTypeRegistry() {
    }

    private static <T extends BiomeSourceGenerator<?>> RegistryObject<BiomeSourceGeneratorType<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new BiomeSourceGeneratorType<>(codec.get()));
    }

    public static IForgeRegistry<BiomeSourceGeneratorType<?>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<BiomeSourceGeneratorType<?>>().setName(LOCATION.location()).setType((Class<BiomeSourceGeneratorType<?>>) (Class<?>) BiomeSourceGeneratorType.class));
    }

}
