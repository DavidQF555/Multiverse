package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.registration.custom.DimensionProviderTypeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.function.Function;

public interface DimensionGenerator {

    Codec<DimensionGenerator> CODEC = ExtraCodecs.lazyInitializedCodec(() -> DimensionProviderTypeRegistry.getRegistry().getCodec().dispatch(DimensionGenerator::getCodec, Function.identity()));

    LevelStem createDimension(RegistryAccess access, long seed, RandomSource random);

    Codec<? extends DimensionGenerator> getCodec();

}
