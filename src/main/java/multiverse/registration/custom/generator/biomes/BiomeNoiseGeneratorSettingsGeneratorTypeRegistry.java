package multiverse.registration.custom.generator.biomes;

import com.mojang.serialization.Codec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsGenerator;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsGeneratorType;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings.TypeMapNoiseGeneratorSettingsGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeNoiseGeneratorSettingsGeneratorTypeRegistry {

    public static final ResourceKey<Registry<BiomeNoiseGeneratorSettingsGeneratorType<?>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "generator/noise_settings_types"));
    public static final DeferredRegister<BiomeNoiseGeneratorSettingsGeneratorType<?>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<BiomeNoiseGeneratorSettingsGeneratorType<TypeMapNoiseGeneratorSettingsGenerator>> TYPE_MAP = register("type_map", () -> TypeMapNoiseGeneratorSettingsGenerator.CODEC);
    private static Supplier<IForgeRegistry<BiomeNoiseGeneratorSettingsGeneratorType<?>>> registry = null;

    private BiomeNoiseGeneratorSettingsGeneratorTypeRegistry() {
    }

    private static <T extends BiomeNoiseGeneratorSettingsGenerator> RegistryObject<BiomeNoiseGeneratorSettingsGeneratorType<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new BiomeNoiseGeneratorSettingsGeneratorType<>(codec.get()));
    }

    public static IForgeRegistry<BiomeNoiseGeneratorSettingsGeneratorType<?>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<BiomeNoiseGeneratorSettingsGeneratorType<?>>().setType((Class<BiomeNoiseGeneratorSettingsGeneratorType<?>>) (Class<?>) BiomeNoiseGeneratorSettingsGeneratorType.class).setName(LOCATION.location()));
    }

}
