package multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.biomes.AddFeaturesAllBiomeModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class BiomeModifierRegistry {

    public static final DeferredRegister<Codec<? extends BiomeModifier>> TYPES = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Multiverse.MOD_ID);

    public static final RegistryObject<Codec<AddFeaturesAllBiomeModifier>> ADD_ALL = register("add_features_all", () -> AddFeaturesAllBiomeModifier.CODEC);

    private BiomeModifierRegistry() {
    }

    private static <T extends BiomeModifier> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

}
