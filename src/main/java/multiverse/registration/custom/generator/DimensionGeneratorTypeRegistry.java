package multiverse.registration.custom.generator;

import com.mojang.serialization.Codec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.DimensionGenerator;
import multiverse.common.world.worldgen.generators.DimensionGeneratorType;
import multiverse.common.world.worldgen.generators.biomes.BiomeConfigDimensionGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DimensionGeneratorTypeRegistry {

    public static final ResourceKey<Registry<DimensionGeneratorType<?>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "generator/dimensions"));
    public static final DeferredRegister<DimensionGeneratorType<?>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<DimensionGeneratorType<BiomeConfigDimensionGenerator>> BIOME_CONFIG = register("biome_config", () -> BiomeConfigDimensionGenerator.CODEC);
    private static Supplier<IForgeRegistry<DimensionGeneratorType<?>>> registry = null;

    private DimensionGeneratorTypeRegistry() {
    }

    private static <T extends DimensionGenerator> RegistryObject<DimensionGeneratorType<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new DimensionGeneratorType<>(codec.get()));
    }

    public static IForgeRegistry<DimensionGeneratorType<?>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<DimensionGeneratorType<?>>().setType((Class<DimensionGeneratorType<?>>) (Class<?>) DimensionGeneratorType.class).setName(LOCATION.location()));
    }

}