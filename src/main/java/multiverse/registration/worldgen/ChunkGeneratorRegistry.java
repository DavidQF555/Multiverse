package multiverse.registration.worldgen;

import com.mojang.serialization.MapCodec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.MultiverseNoiseChunkGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ChunkGeneratorRegistry {

    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, Multiverse.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<MultiverseNoiseChunkGenerator>> MULTIVERSE = register("multiverse", () -> MultiverseNoiseChunkGenerator.CODEC);

    private ChunkGeneratorRegistry() {
    }

    private static <T extends ChunkGenerator> DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return GENERATORS.register(name, codec);
    }

}
