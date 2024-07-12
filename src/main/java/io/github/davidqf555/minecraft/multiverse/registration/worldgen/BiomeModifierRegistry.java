package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.CategoryAddSpawnsBiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class BiomeModifierRegistry {

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Multiverse.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<CategoryAddSpawnsBiomeModifier>> CATEGORY_ADD_SPAWNS = register("category_add_spawns", CategoryAddSpawnsBiomeModifier.CODEC);

    private BiomeModifierRegistry() {
    }

    private static <T extends BiomeModifier> DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<T>> register(String name, MapCodec<T> codec) {
        return SERIALIZERS.register(name, () -> codec);
    }

}
