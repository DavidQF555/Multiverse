package multiverse.registration.custom;

import com.mojang.serialization.Codec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.DimensionGenerator;
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

    public static final ResourceKey<Registry<Codec<? extends DimensionGenerator>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "dimension_generator"));
    public static final DeferredRegister<Codec<? extends DimensionGenerator>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<BiomeConfigDimensionGenerator>> BIOME_CONFIG = register("biome_config", () -> BiomeConfigDimensionGenerator.CODEC);
    private static Supplier<IForgeRegistry<Codec<? extends DimensionGenerator>>> registry = null;

    private DimensionGeneratorTypeRegistry() {
    }

    private static <T extends DimensionGenerator> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static IForgeRegistry<Codec<? extends DimensionGenerator>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Codec<? extends DimensionGenerator>>().setName(LOCATION.location()));
    }

}