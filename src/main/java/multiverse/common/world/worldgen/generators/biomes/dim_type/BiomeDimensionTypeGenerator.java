package multiverse.common.world.worldgen.generators.biomes.dim_type;

import com.mojang.serialization.Codec;
import multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import multiverse.registration.custom.generator.biomes.BiomeDimensionTypeGeneratorRegistry;
import multiverse.registration.custom.generator.biomes.BiomeDimensionTypeGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class BiomeDimensionTypeGenerator extends ForgeRegistryEntry<BiomeDimensionTypeGenerator> implements BiomeFieldGenerator<Holder<DimensionType>> {

    public static final Codec<BiomeDimensionTypeGenerator> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeDimensionTypeGeneratorTypeRegistry.getRegistry().getCodec().dispatch(BiomeDimensionTypeGenerator::getType, BiomeDimensionTypeGeneratorType::getCodec));
    public static final Codec<Holder<BiomeDimensionTypeGenerator>> CODEC = RegistryFileCodec.create(BiomeDimensionTypeGeneratorRegistry.LOCATION, DIRECT_CODEC);

    public abstract BiomeDimensionTypeGeneratorType getType();

}
