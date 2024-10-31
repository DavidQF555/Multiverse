package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.registration.custom.DimensionProviderTypeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.function.Function;

public interface DimensionProvider {

    Codec<DimensionProvider> CODEC = Codec.lazyInitialized(() -> DimensionProviderTypeRegistry.getRegistry().byNameCodec().dispatch(DimensionProvider::getCodec, Function.identity()));

    LevelStem createDimension(RegistryAccess access, long seed, RandomSource random);

    MapCodec<? extends DimensionProvider> getCodec();

}
