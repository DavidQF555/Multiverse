package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomeSource;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class BiomeSourceRegistry {

    public static final DeferredRegister<Codec<? extends BiomeSource>> SOURCES = DeferredRegister.create(Registry.BIOME_SOURCE_REGISTRY, Multiverse.MOD_ID);

    public static final RegistryObject<Codec<MultiverseBiomeSource>> MULTIVERSE = register("multiverse", MultiverseBiomeSource.CODEC);

    private BiomeSourceRegistry() {
    }

    private static <T extends BiomeSource> RegistryObject<Codec<T>> register(String name, Codec<T> codec) {
        return SOURCES.register(name, () -> codec);
    }

}
