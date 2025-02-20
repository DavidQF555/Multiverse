package io.github.davidqf555.minecraft.multiverse.registration.custom;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea_level.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SeaLevelProviderTypeRegistry {

    public static final ResourceKey<Registry<SeaLevelGeneratorType<?>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "sea_level_provider_type"));
    public static final DeferredRegister<SeaLevelGeneratorType<?>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<SeaLevelGeneratorType<FlatSeaLevelGenerator>> FLAT = register("flat", () -> FlatSeaLevelGenerator.CODEC);
    public static final RegistryObject<SeaLevelGeneratorType<WaveSeaLevelGenerator>> WAVE = register("wave", () -> WaveSeaLevelGenerator.CODEC);
    public static final RegistryObject<SeaLevelGeneratorType<WeightedSeaLevelGenerator>> WEIGHTED = register("weighted", () -> WeightedSeaLevelGenerator.CODEC);
    private static Supplier<IForgeRegistry<SeaLevelGeneratorType<?>>> registry = null;

    private SeaLevelProviderTypeRegistry() {
    }

    private static <T extends SeaLevelGenerator> RegistryObject<SeaLevelGeneratorType<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new SeaLevelGeneratorType<>(codec.get()));
    }

    public static IForgeRegistry<SeaLevelGeneratorType<?>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<SeaLevelGeneratorType<?>>().setType((Class<SeaLevelGeneratorType<?>>) (Class<?>) SeaLevelGeneratorType.class).setName(LOCATION.location()));
    }

}
