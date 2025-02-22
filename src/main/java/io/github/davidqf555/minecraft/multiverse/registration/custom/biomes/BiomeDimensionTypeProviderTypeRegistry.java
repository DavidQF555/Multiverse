package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.dim_type.BiomeDimensionTypeProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.dim_type.TypeMapDimensionTypeGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.dim_type.WeightedDimensionTypeGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeDimensionTypeProviderTypeRegistry {

    public static final ResourceKey<Registry<Codec<? extends BiomeDimensionTypeProvider>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "dimension_type_provider_type"));
    public static final DeferredRegister<Codec<? extends BiomeDimensionTypeProvider>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<WeightedDimensionTypeGenerator>> WEIGHTED = register("weighted", () -> WeightedDimensionTypeGenerator.CODEC);
    public static final RegistryObject<Codec<TypeMapDimensionTypeGenerator>> TYPE_MAP = register("type_map", () -> TypeMapDimensionTypeGenerator.CODEC);
    private static Supplier<IForgeRegistry<Codec<? extends BiomeDimensionTypeProvider>>> registry = null;

    private BiomeDimensionTypeProviderTypeRegistry() {
    }

    private static <T extends BiomeDimensionTypeProvider> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static IForgeRegistry<Codec<? extends BiomeDimensionTypeProvider>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Codec<? extends BiomeDimensionTypeProvider>>().setName(LOCATION.location()));
    }

}
