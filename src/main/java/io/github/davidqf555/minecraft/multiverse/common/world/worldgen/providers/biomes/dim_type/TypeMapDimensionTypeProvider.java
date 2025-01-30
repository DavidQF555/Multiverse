package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.dim_type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionTypeProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Arrays;
import java.util.Map;

public class TypeMapDimensionTypeProvider implements BiomeDimensionTypeProvider {

    public static final MapCodec<TypeMapDimensionTypeProvider> CODEC = Codec.simpleMap(MultiverseType.CODEC, BiomeDimensionTypeProvider.CODEC, Keyable.forStrings(() -> Arrays.stream(MultiverseType.values()).map(MultiverseType::getName)))
            .xmap(TypeMapDimensionTypeProvider::new, val -> val.values).fieldOf("providers");
    private final Map<MultiverseType, Holder<BiomeDimensionTypeProvider>> values;

    public TypeMapDimensionTypeProvider(Map<MultiverseType, Holder<BiomeDimensionTypeProvider>> values) {
        this.values = values;
    }

    @Override
    public Holder<DimensionType> provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes) {
        return values.get(type).value().provide(access, seed, random, type, biomes);
    }

    @Override
    public MapCodec<? extends TypeMapDimensionTypeProvider> getCodec() {
        return BiomeDimensionTypeProviderTypeRegistry.TYPE_MAP.get();
    }

}
