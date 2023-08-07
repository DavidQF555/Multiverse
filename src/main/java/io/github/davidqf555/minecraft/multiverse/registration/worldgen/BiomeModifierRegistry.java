package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.CategoryAddSpawnsBiomeModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BiomeModifierRegistry {

    public static final DeferredRegister<Codec<? extends BiomeModifier>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Multiverse.MOD_ID);

    public static final RegistryObject<Codec<CategoryAddSpawnsBiomeModifier>> CATEGORY_ADD_SPAWNS = register("category_add_spawns", CategoryAddSpawnsBiomeModifier.CODEC);

    private BiomeModifierRegistry() {
    }

    private static <T extends BiomeModifier> RegistryObject<Codec<T>> register(String name, Codec<T> codec) {
        return SERIALIZERS.register(name, () -> codec);
    }

}
