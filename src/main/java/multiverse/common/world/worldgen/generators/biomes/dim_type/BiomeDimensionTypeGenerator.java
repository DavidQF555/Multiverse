package multiverse.common.world.worldgen.generators.biomes.dim_type;

import com.mojang.serialization.Codec;
import multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import multiverse.registration.custom.biomes.BiomeDimensionTypeGeneratorRegistry;
import multiverse.registration.custom.biomes.BiomeDimensionTypeGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.function.Function;

public interface BiomeDimensionTypeGenerator extends BiomeFieldGenerator<Holder<DimensionType>> {

    Codec<BiomeDimensionTypeGenerator> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeDimensionTypeGeneratorTypeRegistry.getRegistry().getCodec().dispatch(BiomeDimensionTypeGenerator::getCodec, Function.identity()));
    Codec<Holder<BiomeDimensionTypeGenerator>> CODEC = RegistryFileCodec.create(BiomeDimensionTypeGeneratorRegistry.LOCATION, DIRECT_CODEC);

    Codec<? extends BiomeDimensionTypeGenerator> getCodec();

}
