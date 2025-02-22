package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.DimensionGenerator;
import io.github.davidqf555.minecraft.multiverse.registration.custom.DimensionProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

public class BiomeConfigDimensionGenerator implements DimensionGenerator {

    public static final Codec<BiomeConfigDimensionGenerator> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeConfig.CODEC.fieldOf("biomes").forGetter(val -> val.config),
            BiomeDimensionProvider.CODEC.fieldOf("dimension").forGetter(val -> val.provider)
    ).apply(inst, BiomeConfigDimensionGenerator::new));
    private final Holder<BiomeConfig> config;
    private final BiomeDimensionProvider provider;

    public BiomeConfigDimensionGenerator(Holder<BiomeConfig> config, BiomeDimensionProvider provider) {
        this.config = config;
        this.provider = provider;
    }

    @Override
    public LevelStem createDimension(RegistryAccess access, long seed, RandomSource random) {
        BiomeConfig config = this.config.value();
        Pair<MultiverseType, HolderSet<Biome>> biomes = config.selectRandom(random);
        return provider.provide(access, seed, random, biomes.getFirst(), biomes.getSecond());
    }

    @Override
    public Codec<? extends DimensionGenerator> getCodec() {
        return DimensionProviderTypeRegistry.BIOME_CONFIG.get();
    }

}
