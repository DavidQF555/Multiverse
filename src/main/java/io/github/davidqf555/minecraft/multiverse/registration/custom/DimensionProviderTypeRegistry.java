package io.github.davidqf555.minecraft.multiverse.registration.custom;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.DimensionProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.DimensionProviderType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.BiomeConfigDimensionProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DimensionProviderTypeRegistry {

    public static final ResourceKey<Registry<DimensionProviderType<?>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "dimension_provider"));
    public static final DeferredRegister<DimensionProviderType<?>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<DimensionProviderType<BiomeConfigDimensionProvider>> BIOME_CONFIG = register("biome_config", () -> BiomeConfigDimensionProvider.CODEC);
    private static Supplier<IForgeRegistry<DimensionProviderType<?>>> registry = null;

    private DimensionProviderTypeRegistry() {
    }

    private static <T extends DimensionProvider> RegistryObject<DimensionProviderType<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new DimensionProviderType<>(codec.get()));
    }

    public static IForgeRegistry<DimensionProviderType<?>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<DimensionProviderType<?>>().setType((Class<DimensionProviderType<?>>) (Class<?>) DimensionProviderType.class).setName(LOCATION.location()));
    }

}