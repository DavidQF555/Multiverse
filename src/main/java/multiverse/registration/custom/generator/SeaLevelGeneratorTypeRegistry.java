package multiverse.registration.custom.generator;

import com.mojang.serialization.Codec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.FlatSeaLevelGenerator;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.SeaLevelGenerator;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.WaveSeaLevelGenerator;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.WeightedSeaLevelGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SeaLevelGeneratorTypeRegistry {

    public static final ResourceKey<Registry<Codec<? extends SeaLevelGenerator>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "generator/sea_level_types"));
    public static final DeferredRegister<Codec<? extends SeaLevelGenerator>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<FlatSeaLevelGenerator>> FLAT = register("flat", () -> FlatSeaLevelGenerator.CODEC);
    public static final RegistryObject<Codec<WaveSeaLevelGenerator>> WAVE = register("wave", () -> WaveSeaLevelGenerator.CODEC);
    public static final RegistryObject<Codec<WeightedSeaLevelGenerator>> WEIGHTED = register("weighted", () -> WeightedSeaLevelGenerator.CODEC);
    private static Supplier<IForgeRegistry<Codec<? extends SeaLevelGenerator>>> registry = null;

    private SeaLevelGeneratorTypeRegistry() {
    }

    private static <T extends SeaLevelGenerator> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static IForgeRegistry<Codec<? extends SeaLevelGenerator>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Codec<? extends SeaLevelGenerator>>().setName(LOCATION.location()));
    }

}
