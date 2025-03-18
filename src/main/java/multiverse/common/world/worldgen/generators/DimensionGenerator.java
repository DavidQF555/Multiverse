package multiverse.common.world.worldgen.generators;

import com.mojang.serialization.Codec;
import multiverse.registration.custom.generator.DimensionGeneratorTypeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomSource;

public interface DimensionGenerator {

    Codec<DimensionGenerator> CODEC = ExtraCodecs.lazyInitializedCodec(() -> DimensionGeneratorTypeRegistry.getRegistry().getCodec().dispatch(DimensionGenerator::getType, DimensionGeneratorType::getCodec));

    LevelStem createDimension(RegistryAccess access, long seed, RandomSource random);

    DimensionGeneratorType<? extends DimensionGenerator> getType();

}
