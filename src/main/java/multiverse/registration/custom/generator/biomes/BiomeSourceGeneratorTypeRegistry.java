package multiverse.registration.custom.generator.biomes;

import com.mojang.serialization.Codec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source.BiomeSourceGenerator;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source.NoiseBiomeSourceGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeSourceGeneratorTypeRegistry {

    public static final ResourceKey<Registry<Codec<? extends BiomeSourceGenerator<?>>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "generator/biome_sources"));
    public static final DeferredRegister<Codec<? extends BiomeSourceGenerator<?>>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<NoiseBiomeSourceGenerator>> NOISE = register("noise", NoiseBiomeSourceGenerator.CODEC);
    private static Supplier<IForgeRegistry<Codec<? extends BiomeSourceGenerator<?>>>> registry = null;

    private BiomeSourceGeneratorTypeRegistry() {
    }

    private static <T extends BiomeSourceGenerator<?>> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static IForgeRegistry<Codec<? extends BiomeSourceGenerator<?>>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Codec<? extends BiomeSourceGenerator<?>>>().setName(LOCATION.location()));
    }

}
