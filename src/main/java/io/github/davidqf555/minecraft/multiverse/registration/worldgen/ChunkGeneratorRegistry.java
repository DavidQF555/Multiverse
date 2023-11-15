package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ChunkGeneratorRegistry {

    public static final DeferredRegister<Codec<? extends ChunkGenerator>> GENERATORS = DeferredRegister.create(Registry.CHUNK_GENERATOR_REGISTRY, Multiverse.MOD_ID);

    public static final RegistryObject<Codec<MultiverseChunkGenerator>> MULTIVERSE = register("multiverse", MultiverseChunkGenerator.CODEC);

    private ChunkGeneratorRegistry() {
    }

    private static <T extends ChunkGenerator> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return GENERATORS.register(name, codec);
    }

}
