package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.BiomeDimensionGeneratorType;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.BiomeDimensionProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.DualBiomeDimensionGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeDimensionProviderTypeRegistry {

    public static final ResourceKey<Registry<BiomeDimensionGeneratorType>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "biome_dimension_provider"));
    public static final DeferredRegister<BiomeDimensionGeneratorType> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<BiomeDimensionGeneratorType> DUAL = register("dual", () -> DualBiomeDimensionGenerator.CODEC);
    private static Supplier<IForgeRegistry<BiomeDimensionGeneratorType>> registry = null;

    private BiomeDimensionProviderTypeRegistry() {
    }

    private static <T extends BiomeDimensionProvider> RegistryObject<BiomeDimensionGeneratorType> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new BiomeDimensionGeneratorType(codec.get()));
    }

    public static IForgeRegistry<BiomeDimensionGeneratorType> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<BiomeDimensionGeneratorType>().setName(LOCATION.location()).setType(BiomeDimensionGeneratorType.class));
    }

}
