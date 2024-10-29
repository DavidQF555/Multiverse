package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.BiomeFieldProvider;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionTypeProviderRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionTypeProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.function.Function;

public interface BiomeDimensionTypeProvider extends BiomeFieldProvider<Holder<DimensionType>> {

    Codec<BiomeDimensionTypeProvider> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeDimensionTypeProviderTypeRegistry.getRegistry().getCodec().dispatch(BiomeDimensionTypeProvider::getCodec, Function.identity()));
    Codec<Holder<BiomeDimensionTypeProvider>> CODEC = RegistryFileCodec.create(BiomeDimensionTypeProviderRegistry.LOCATION, DIRECT_CODEC);

    Codec<? extends BiomeDimensionTypeProvider> getCodec();

}
