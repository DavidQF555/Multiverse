package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.BiomeConfig;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.DimensionProvider;
import io.github.davidqf555.minecraft.multiverse.registration.custom.DimensionProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Set;

public class BiomeConfigDimensionProvider implements DimensionProvider {

    public static final MapCodec<BiomeConfigDimensionProvider> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BiomeConfig.CODEC.fieldOf("biomes").forGetter(val -> val.config),
            BiomeDimensionProvider.CODEC.fieldOf("dimension").forGetter(val -> val.provider)
    ).apply(inst, BiomeConfigDimensionProvider::new));
    private final Holder<BiomeConfig> config;
    private final BiomeDimensionProvider provider;

    public BiomeConfigDimensionProvider(Holder<BiomeConfig> config, BiomeDimensionProvider provider) {
        this.config = config;
        this.provider = provider;
    }

    @Override
    public LevelStem createDimension(RegistryAccess access, long seed, RandomSource random) {
        BiomeConfig config = this.config.value();
        Pair<MultiverseType, Set<HolderSet<Biome>>> biomes = config.selectRandom(access.lookupOrThrow(Registries.BIOME), random);
        return provider.provide(access, seed, random, biomes.getFirst(), biomes.getSecond());
    }

    @Override
    public MapCodec<? extends BiomeConfigDimensionProvider> getCodec() {
        return DimensionProviderTypeRegistry.BIOME_CONFIG.get();
    }

}
