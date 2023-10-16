package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.FlatSeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.SeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.WaveSeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.WeightedSeaLevelSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SeaLevelSelectorRegistry {

    public static final ResourceLocation REGISTRY_LOCATION = new ResourceLocation(Multiverse.MOD_ID, "sea_level_selector");
    public static final DeferredRegister<Codec<? extends SeaLevelSelector>> CODECS = DeferredRegister.create(REGISTRY_LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<FlatSeaLevelSelector>> FLAT = register("flat", () -> FlatSeaLevelSelector.CODEC);
    public static final RegistryObject<Codec<WaveSeaLevelSelector>> WAVE = register("wave", () -> WaveSeaLevelSelector.CODEC);
    public static final RegistryObject<Codec<WeightedSeaLevelSelector>> WEIGHTED = register("weighted", WeightedSeaLevelSelector.CODEC);
    private static IForgeRegistry<Codec<? extends SeaLevelSelector>> registry;

    private SeaLevelSelectorRegistry() {
    }

    public static <T extends SeaLevelSelector> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return CODECS.register(name, codec);
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        event.create(new RegistryBuilder<Codec<? extends SeaLevelSelector>>().setName(REGISTRY_LOCATION), r -> registry = r);
    }

    @Nullable
    public static IForgeRegistry<Codec<? extends SeaLevelSelector>> getRegistry() {
        return registry;
    }

}

