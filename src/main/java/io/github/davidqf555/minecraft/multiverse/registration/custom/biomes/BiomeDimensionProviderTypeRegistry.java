package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.BiomeDimensionProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.DualBiomeDimensionProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeDimensionProviderTypeRegistry {

    public static final ResourceKey<Registry<Codec<? extends BiomeDimensionProvider>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "biome_dimension_provider"));
    public static final DeferredRegister<Codec<? extends BiomeDimensionProvider>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<DualBiomeDimensionProvider>> DUAL = register("dual", () -> DualBiomeDimensionProvider.CODEC);
    private static Supplier<IForgeRegistry<Codec<? extends BiomeDimensionProvider>>> registry = null;

    private BiomeDimensionProviderTypeRegistry() {
    }

    private static <T extends BiomeDimensionProvider> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static IForgeRegistry<Codec<? extends BiomeDimensionProvider>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Codec<? extends BiomeDimensionProvider>>().setName(LOCATION.location()));
    }

}
