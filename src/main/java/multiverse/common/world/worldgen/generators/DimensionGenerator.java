package multiverse.common.world.worldgen.generators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import multiverse.registration.custom.DimensionGeneratorTypeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.function.Function;

public interface DimensionGenerator {

    Codec<DimensionGenerator> CODEC = Codec.lazyInitialized(() -> DimensionGeneratorTypeRegistry.getRegistry().byNameCodec().dispatch(DimensionGenerator::getCodec, Function.identity()));

    LevelStem createDimension(RegistryAccess access, long seed, RandomSource random);

    MapCodec<? extends DimensionGenerator> getCodec();

}
