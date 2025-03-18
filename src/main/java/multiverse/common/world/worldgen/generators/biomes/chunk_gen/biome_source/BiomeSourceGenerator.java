package multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source;

import com.mojang.serialization.Codec;
import multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import multiverse.registration.custom.generator.biomes.BiomeSourceGeneratorTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.BiomeSource;

import java.util.function.Function;

public interface BiomeSourceGenerator<T extends BiomeSource> extends BiomeFieldGenerator<T> {

    Codec<BiomeSourceGenerator<?>> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeSourceGeneratorTypeRegistry.getRegistry().getCodec().dispatch(BiomeSourceGenerator::getCodec, Function.identity()));

    Codec<? extends BiomeSourceGenerator<T>> getCodec();

}
