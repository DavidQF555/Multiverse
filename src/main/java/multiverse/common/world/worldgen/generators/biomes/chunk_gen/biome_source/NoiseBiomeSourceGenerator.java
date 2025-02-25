package multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.common.world.worldgen.MultiverseType;
import multiverse.common.world.worldgen.biomes.LazyMultiverseBiomeSource;
import multiverse.common.world.worldgen.generators.GeneratorSettings;
import multiverse.registration.custom.biomes.BiomeSourceGeneratorTypeRegistry;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

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
        GeneratorSettings settings = GeneratorSettings.getSettings();
        return new LazyMultiverseBiomeSource(access.lookupOrThrow(Registries.BIOME), access.lookupOrThrow(Registries.DIMENSION_TYPE), minY, maxY, seed, settings.temperature(), settings.humidity(), type, biomes);
    }

    @Override
    public Codec<? extends NoiseBiomeSourceGenerator> getCodec() {
        return BiomeSourceGeneratorTypeRegistry.NOISE.get();
    }

}
