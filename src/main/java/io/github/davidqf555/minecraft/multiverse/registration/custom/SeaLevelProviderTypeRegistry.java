package io.github.davidqf555.minecraft.multiverse.registration.custom;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.sea_level.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SeaLevelProviderTypeRegistry {

    public static final ResourceKey<Registry<SeaLevelProviderType<?>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "sea_level_provider_type"));
    public static final DeferredRegister<SeaLevelProviderType<?>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<SeaLevelProviderType<FlatSeaLevelProvider>> FLAT = register("flat", () -> FlatSeaLevelProvider.CODEC);
    public static final RegistryObject<SeaLevelProviderType<WaveSeaLevelProvider>> WAVE = register("wave", () -> WaveSeaLevelProvider.CODEC);
    public static final RegistryObject<SeaLevelProviderType<WeightedSeaLevelProvider>> WEIGHTED = register("weighted", () -> WeightedSeaLevelProvider.CODEC);
    private static Supplier<IForgeRegistry<SeaLevelProviderType<?>>> registry = null;

    private SeaLevelProviderTypeRegistry() {
    }

    private static <T extends SeaLevelProvider> RegistryObject<SeaLevelProviderType<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new SeaLevelProviderType<>(codec.get()));
    }

    public static IForgeRegistry<SeaLevelProviderType<?>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<SeaLevelProviderType<?>>().setType((Class<SeaLevelProviderType<?>>) (Class<?>) SeaLevelProviderType.class).setName(LOCATION.location()));
    }

}
