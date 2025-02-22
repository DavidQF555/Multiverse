package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionGeneratorTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.LevelStem;

public interface BiomeDimensionGenerator extends BiomeFieldGenerator<LevelStem> {

    Codec<BiomeDimensionGenerator> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeDimensionGeneratorTypeRegistry.getRegistry().getCodec().dispatch(BiomeDimensionGenerator::getType, BiomeDimensionGeneratorType::getCodec));

    BiomeDimensionGeneratorType getType();

}
