package multiverse.common.world.worldgen.generators.biomes;

import com.mojang.serialization.Codec;
import multiverse.registration.custom.biomes.BiomeDimensionGeneratorTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.function.Function;

public interface BiomeDimensionGenerator extends BiomeFieldGenerator<LevelStem> {

    Codec<BiomeDimensionGenerator> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeDimensionGeneratorTypeRegistry.getRegistry().getCodec().dispatch(BiomeDimensionGenerator::getCodec, Function.identity()));

    Codec<? extends BiomeDimensionGenerator> getCodec();

}
