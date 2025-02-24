package multiverse.registration.worldgen;

import com.mojang.serialization.MapCodec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.biomes.AddFeaturesAllBiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class BiomeModifierRegistry {

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Multiverse.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<AddFeaturesAllBiomeModifier>> ADD_ALL = register("add_features_all", () -> AddFeaturesAllBiomeModifier.CODEC);

    private BiomeModifierRegistry() {
    }

    private static <T extends BiomeModifier> DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

}
