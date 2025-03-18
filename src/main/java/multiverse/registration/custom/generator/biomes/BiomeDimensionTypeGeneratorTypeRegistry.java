package multiverse.registration.custom.generator.biomes;

import com.mojang.serialization.Codec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.dim_type.BiomeDimensionTypeGenerator;
import multiverse.common.world.worldgen.generators.biomes.dim_type.TypeMapDimensionTypeGenerator;
import multiverse.common.world.worldgen.generators.biomes.dim_type.WeightedDimensionTypeGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeDimensionTypeGeneratorTypeRegistry {

    public static final ResourceKey<Registry<Codec<? extends BiomeDimensionTypeGenerator>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "generator/dimension_type_types"));
    public static final DeferredRegister<Codec<? extends BiomeDimensionTypeGenerator>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<WeightedDimensionTypeGenerator>> WEIGHTED = register("weighted", () -> WeightedDimensionTypeGenerator.CODEC);
    public static final RegistryObject<Codec<TypeMapDimensionTypeGenerator>> TYPE_MAP = register("type_map", () -> TypeMapDimensionTypeGenerator.CODEC);
    private static Supplier<IForgeRegistry<Codec<? extends BiomeDimensionTypeGenerator>>> registry = null;

    private BiomeDimensionTypeGeneratorTypeRegistry() {
    }

    private static <T extends BiomeDimensionTypeGenerator> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static IForgeRegistry<Codec<? extends BiomeDimensionTypeGenerator>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Codec<? extends BiomeDimensionTypeGenerator>>().setName(LOCATION.location()));
    }

}
