package multiverse.registration.custom.biomes;

import com.mojang.serialization.Codec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsGenerator;
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

    public static final ResourceKey<Registry<Codec<? extends BiomeNoiseGeneratorSettingsGenerator>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "noise_settings_generator_type"));
    public static final DeferredRegister<Codec<? extends BiomeNoiseGeneratorSettingsGenerator>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<TypeMapNoiseGeneratorSettingsGenerator>> TYPE_MAP = register("type_map", () -> TypeMapNoiseGeneratorSettingsGenerator.CODEC);
    private static Supplier<IForgeRegistry<Codec<? extends BiomeNoiseGeneratorSettingsGenerator>>> registry = null;

    private BiomeNoiseGeneratorSettingsGeneratorTypeRegistry() {
    }

    private static <T extends BiomeNoiseGeneratorSettingsGenerator> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static IForgeRegistry<Codec<? extends BiomeNoiseGeneratorSettingsGenerator>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Codec<? extends BiomeNoiseGeneratorSettingsGenerator>>().setName(LOCATION.location()));
    }

}
