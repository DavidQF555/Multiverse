package io.github.davidqf555.minecraft.multiverse.registration.custom;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SeaLevelSelectorTypeRegistry {

    public static final ResourceKey<Registry<SeaLevelSelectorType<?>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "sea_level_selector_type"));
    public static final DeferredRegister<SeaLevelSelectorType<?>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<SeaLevelSelectorType<FlatSeaLevelSelector>> FLAT = register("flat", () -> FlatSeaLevelSelector.CODEC);
    public static final RegistryObject<SeaLevelSelectorType<WaveSeaLevelSelector>> WAVE = register("wave", () -> WaveSeaLevelSelector.CODEC);
    public static final RegistryObject<SeaLevelSelectorType<WeightedSeaLevelSelector>> WEIGHTED = register("weighted", () -> WeightedSeaLevelSelector.CODEC);
    private static Supplier<IForgeRegistry<SeaLevelSelectorType<?>>> registry = null;

    private SeaLevelSelectorTypeRegistry() {
    }

    private static <T extends SeaLevelSelector> RegistryObject<SeaLevelSelectorType<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new SeaLevelSelectorType<>(codec.get()));
    }

    public static IForgeRegistry<SeaLevelSelectorType<?>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<SeaLevelSelectorType<?>>().setType((Class<SeaLevelSelectorType<?>>) (Class<?>) SeaLevelSelectorType.class).setName(LOCATION.location()));
    }

}
