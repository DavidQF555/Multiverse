package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.BiomeFieldProvider;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionTypeProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionType;

public interface BiomeDimensionTypeProvider extends BiomeFieldProvider<Holder<DimensionType>> {

    Codec<BiomeDimensionTypeProvider> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeDimensionTypeProviderTypeRegistry.getRegistry().getCodec().dispatch(BiomeDimensionTypeProvider::getType, BiomeDimensionTypeProviderType::getCodec));

    BiomeDimensionTypeProviderType getType();

}
