package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.LazyMultiverseBiomeSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class BiomeSourceRegistry {

    public static final DeferredRegister<Codec<? extends BiomeSource>> SOURCES = DeferredRegister.create(Registries.BIOME_SOURCE, Multiverse.MOD_ID);

    public static final RegistryObject<Codec<LazyMultiverseBiomeSource>> LAZY_MULTIVERSE = register("lazy_multiverse", LazyMultiverseBiomeSource.CODEC);

    private BiomeSourceRegistry() {
    }

    private static <T extends BiomeSource> RegistryObject<Codec<T>> register(String name, Codec<T> codec) {
        return SOURCES.register(name, () -> codec);
    }

}