package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.LazyMultiverseBiomeSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BiomeSourceRegistry {

    public static final DeferredRegister<MapCodec<? extends BiomeSource>> SOURCES = DeferredRegister.create(Registries.BIOME_SOURCE, Multiverse.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<LazyMultiverseBiomeSource>> LAZY_MULTIVERSE = register("lazy_multiverse", LazyMultiverseBiomeSource.CODEC);

    private BiomeSourceRegistry() {
    }

    private static <T extends BiomeSource> DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<T>> register(String name, MapCodec<T> codec) {
        return SOURCES.register(name, () -> codec);
    }

}