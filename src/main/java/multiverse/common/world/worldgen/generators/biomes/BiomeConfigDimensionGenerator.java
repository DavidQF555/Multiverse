package multiverse.common.world.worldgen.generators.biomes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.common.world.worldgen.MultiverseType;
import multiverse.common.world.worldgen.generators.DimensionGenerator;
import multiverse.common.world.worldgen.generators.DimensionGeneratorType;
import multiverse.registration.custom.generator.DimensionGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomSource;

public class BiomeConfigDimensionGenerator implements DimensionGenerator {

    public static final Codec<BiomeConfigDimensionGenerator> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeConfig.CODEC.fieldOf("biomes").forGetter(val -> val.config),
            BiomeDimensionGenerator.CODEC.fieldOf("dimension").forGetter(val -> val.provider)
    ).apply(inst, BiomeConfigDimensionGenerator::new));
    private final Holder<BiomeConfig> config;
    private final BiomeDimensionGenerator provider;

    public BiomeConfigDimensionGenerator(Holder<BiomeConfig> config, BiomeDimensionGenerator provider) {
        this.config = config;
        this.provider = provider;
    }

    @Override
    public LevelStem createDimension(RegistryAccess access, long seed, RandomSource random) {
        BiomeConfig config = this.config.value();
        Pair<MultiverseType, HolderSet<Biome>> biomes = config.selectRandom(random);
        return provider.generate(access, seed, random, biomes.getFirst(), biomes.getSecond());
    }

    @Override
    public DimensionGeneratorType<? extends DimensionGenerator> getType() {
        return DimensionGeneratorTypeRegistry.BIOME_CONFIG.get();
    }

}
