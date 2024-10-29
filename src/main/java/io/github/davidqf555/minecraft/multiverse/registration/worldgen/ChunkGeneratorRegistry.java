package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseNoiseChunkGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ChunkGeneratorRegistry {

    public static final DeferredRegister<Codec<? extends ChunkGenerator>> GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, Multiverse.MOD_ID);

    public static final RegistryObject<Codec<MultiverseNoiseChunkGenerator>> MULTIVERSE = register("multiverse", () -> MultiverseNoiseChunkGenerator.CODEC);

    private ChunkGeneratorRegistry() {
    }

    private static <T extends ChunkGenerator> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return GENERATORS.register(name, codec);
    }

}
