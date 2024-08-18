package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type.BiomeDimensionTypeProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type.BiomeDimensionTypeProviderType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type.GenericDimensionTypeProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeDimensionTypeProviderTypeRegistry {

    public static final ResourceKey<Registry<BiomeDimensionTypeProviderType>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "dimension_type_provider"));
    public static final DeferredRegister<BiomeDimensionTypeProviderType> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<BiomeDimensionTypeProviderType> GENERIC = register("generic", () -> GenericDimensionTypeProvider.CODEC);
    private static Supplier<IForgeRegistry<BiomeDimensionTypeProviderType>> registry = null;

    private BiomeDimensionTypeProviderTypeRegistry() {
    }

    private static <T extends BiomeDimensionTypeProvider> RegistryObject<BiomeDimensionTypeProviderType> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new BiomeDimensionTypeProviderType(codec.get()));
    }

    public static IForgeRegistry<BiomeDimensionTypeProviderType> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<BiomeDimensionTypeProviderType>().setType(BiomeDimensionTypeProviderType.class).setName(LOCATION.location()));
    }

}
