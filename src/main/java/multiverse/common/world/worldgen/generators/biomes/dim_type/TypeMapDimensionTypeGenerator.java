package multiverse.common.world.worldgen.generators.biomes.dim_type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import multiverse.common.world.worldgen.MultiverseType;
import multiverse.registration.custom.biomes.BiomeDimensionTypeGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Arrays;
import java.util.Map;

public class TypeMapDimensionTypeGenerator implements BiomeDimensionTypeGenerator {

    public static final Codec<TypeMapDimensionTypeGenerator> CODEC = Codec.simpleMap(MultiverseType.CODEC, BiomeDimensionTypeGenerator.CODEC, Keyable.forStrings(() -> Arrays.stream(MultiverseType.values()).map(MultiverseType::getName)))
            .xmap(TypeMapDimensionTypeGenerator::new, val -> val.values).fieldOf("providers").codec();
    private final Map<MultiverseType, Holder<BiomeDimensionTypeGenerator>> values;

    public TypeMapDimensionTypeGenerator(Map<MultiverseType, Holder<BiomeDimensionTypeGenerator>> values) {
        this.values = values;
    }

    @Override
    public Holder<DimensionType> generate(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes) {
        return values.get(type).value().generate(access, seed, random, type, biomes);
    }

    @Override
    public Codec<? extends TypeMapDimensionTypeGenerator> getCodec() {
        return BiomeDimensionTypeGeneratorTypeRegistry.TYPE_MAP.get();
    }

}
