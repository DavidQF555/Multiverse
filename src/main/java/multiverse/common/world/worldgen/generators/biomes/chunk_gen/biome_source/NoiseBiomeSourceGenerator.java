package multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.common.world.worldgen.MultiverseType;
import multiverse.common.world.worldgen.biomes.LazyMultiverseBiomeSource;
import multiverse.registration.custom.biomes.BiomeSourceGeneratorTypeRegistry;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.RandomSource;

import java.util.function.Supplier;

public class NoiseBiomeSourceGenerator implements BiomeSourceGenerator<LazyMultiverseBiomeSource> {

    public static final Supplier<Codec<NoiseBiomeSourceGenerator>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("min_y").forGetter(val -> val.minY),
            Codec.INT.fieldOf("max_y").forGetter(val -> val.maxY)
    ).apply(inst, NoiseBiomeSourceGenerator::new)));
    private final int minY, maxY;

    public NoiseBiomeSourceGenerator(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public LazyMultiverseBiomeSource generate(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes) {
        return new LazyMultiverseBiomeSource(access.registryOrThrow(Registry.BIOME_REGISTRY), access.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), minY, maxY, type, biomes);
    }

    @Override
    public BiomeSourceGeneratorType<? extends NoiseBiomeSourceGenerator> getType() {
        return BiomeSourceGeneratorTypeRegistry.NOISE.get();
    }

}
