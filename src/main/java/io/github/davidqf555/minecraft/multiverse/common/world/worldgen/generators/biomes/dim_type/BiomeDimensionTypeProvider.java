package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.dim_type;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionTypeProviderRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionTypeProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class BiomeDimensionTypeProvider extends ForgeRegistryEntry<BiomeDimensionTypeProvider> implements BiomeFieldGenerator<Holder<DimensionType>> {

    public static final Codec<BiomeDimensionTypeProvider> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeDimensionTypeProviderTypeRegistry.getRegistry().getCodec().dispatch(BiomeDimensionTypeProvider::getType, BiomeDimensionTypeGeneratorType::getCodec));
    public static final Codec<Holder<BiomeDimensionTypeProvider>> CODEC = RegistryFileCodec.create(BiomeDimensionTypeProviderRegistry.LOCATION, DIRECT_CODEC);

    public abstract BiomeDimensionTypeGeneratorType getType();

}
