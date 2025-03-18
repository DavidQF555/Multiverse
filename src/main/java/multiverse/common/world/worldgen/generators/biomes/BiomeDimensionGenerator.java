package multiverse.common.world.worldgen.generators.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import multiverse.registration.custom.generator.biomes.BiomeDimensionGeneratorTypeRegistry;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.function.Function;

public interface BiomeDimensionGenerator extends BiomeFieldGenerator<LevelStem> {

    Codec<BiomeDimensionGenerator> CODEC = Codec.lazyInitialized(() -> BiomeDimensionGeneratorTypeRegistry.getRegistry().byNameCodec().dispatch(BiomeDimensionGenerator::getCodec, Function.identity()));

    MapCodec<? extends BiomeDimensionGenerator> getCodec();

}
