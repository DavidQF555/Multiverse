package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.registration.custom.DimensionProviderTypeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomSource;

public interface DimensionProvider {

    Codec<DimensionProvider> CODEC = ExtraCodecs.lazyInitializedCodec(() -> DimensionProviderTypeRegistry.getRegistry().getCodec().dispatch(DimensionProvider::getType, DimensionProviderType::getCodec));

    LevelStem createDimension(RegistryAccess access, long seed, RandomSource random);

    DimensionProviderType<? extends DimensionProvider> getType();

}
