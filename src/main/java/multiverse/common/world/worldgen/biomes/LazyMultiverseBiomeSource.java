package multiverse.common.world.worldgen.biomes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.common.util.MultiverseConfig;
import multiverse.common.world.DimensionHelper;
import multiverse.common.world.worldgen.MultiverseType;
import multiverse.registration.worldgen.BiomeSourceRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;

import java.util.ArrayList;
import java.util.List;

public class LazyMultiverseBiomeSource extends LazyBiomeSource {

    private static final long FACTOR = 5555555555L;
    private static final long OFFSET = 55555L;
    public static final Codec<LazyMultiverseBiomeSource> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(source -> source.registry),
            RegistryOps.retrieveRegistry(Registry.DIMENSION_TYPE_REGISTRY).forGetter(source -> source.dimType),
            Codec.INT.fieldOf("min_y").forGetter(source -> source.minY),
            Codec.INT.fieldOf("max_y").forGetter(source -> source.maxY),
            Codec.LONG.fieldOf("seed").forGetter(source -> source.seed),
            Codec.DOUBLE.optionalFieldOf("temperature", 0.0).forGetter(source -> source.temperature),
            Codec.DOUBLE.optionalFieldOf("humidity", 0.0).forGetter(source -> source.humidity),
            MultiverseType.CODEC.fieldOf("multiverse_type").forGetter(source -> source.type),
            RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY, true).fieldOf("biomes").forGetter(source -> source.biomes)
    ).apply(inst, LazyMultiverseBiomeSource::new));
    private final Registry<Biome> registry;
    private final Registry<DimensionType> dimType;
    private final MultiverseType type;
    private final HolderSet<Biome> biomes;
    private final int minY, maxY;
    private final double temperature, humidity;
    private final long seed;

    public LazyMultiverseBiomeSource(Registry<Biome> registry, Registry<DimensionType> dimType, int minY, int maxY, long seed, double temperature, double humidity, MultiverseType type, HolderSet<Biome> biomes) {
        super(() -> new MultiNoiseBiomeSource(parameters(registry, dimType, minY, maxY, seed, temperature, humidity, type, biomes)));
        this.registry = registry;
        this.dimType = dimType;
        this.type = type;
        this.biomes = biomes;
        this.minY = minY;
        this.maxY = maxY;
        this.temperature = temperature;
        this.humidity = humidity;
        this.seed = seed;
    }

    public static Climate.ParameterList<Holder<Biome>> parameters(Registry<Biome> registry, Registry<DimensionType> dimType, int minY, int maxY, long seed, double temperature, double humidity, MultiverseType type, HolderSet<Biome> biomes) {
        MultiverseBiomes ref = MultiverseConfig.getBiomesManager();
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> all = new ArrayList<>();
        RandomSource random = new SingleThreadedRandomSource(0);
        for (Holder<Biome> holder : biomes) {
            holder.unwrapKey().ifPresent(key -> {
                if (ref.is(type, key)) {
                    random.setSeed(DimensionHelper.resourceLocationToSeed(key.location(), seed + OFFSET, FACTOR));
                    for (Climate.ParameterPoint orig : ref.getParameters(key)) {
                        Climate.Parameter depth = translateDepth(orig.depth(), minY, maxY, dimType.getOrThrow(type.getNormalType()));
                        Climate.Parameter temp = offset(orig.temperature(), random, temperature);
                        Climate.Parameter humid = offset(orig.humidity(), random, humidity);
                        Climate.ParameterPoint point = new Climate.ParameterPoint(temp, humid, orig.continentalness(), orig.erosion(), depth, orig.weirdness(), orig.offset());
                        all.add(Pair.of(point, holder));
                    }
                }
            });
        }
        if (all.isEmpty()) {
            all.add(Pair.of(Climate.parameters(0, 0, 0, 0, 0, 0, 0), registry.getHolderOrThrow(Biomes.THE_VOID)));
        }
        return new Climate.ParameterList<>(all);
    }

    private static Climate.Parameter offset(Climate.Parameter parameter, RandomSource random, double range) {
        float offset = (float) (random.nextGaussian() * range);
        return Climate.Parameter.span(
                Mth.clamp(Climate.unquantizeCoord(parameter.min()) + offset, -2, 2),
                Mth.clamp(Climate.unquantizeCoord(parameter.max()) + offset, -2, 2)
        );
    }

    //needed because depth function has a constant lerp of y from -64 to 320, scaled from 1.5 to -1.5
    private static Climate.Parameter translateDepth(Climate.Parameter depth, int minY, int maxY, DimensionType from) {
        double start = Climate.unquantizeCoord(depth.min());
        double end = Climate.unquantizeCoord(depth.max());

        double fDepthStart = Mth.clampedMap(from.minY(), -64, 320, 1.5, -1.5);
        double fDepthEnd = Mth.clampedMap(from.minY() + from.height(), -64, 320, 1.5, -1.5);

        double fStartFactor = Mth.inverseLerp(start, fDepthStart, fDepthEnd);
        double fEndFactor = Mth.inverseLerp(end, fDepthStart, fDepthEnd);

        double tDepthStart = Mth.clampedMap(minY, -64, 320, 1.5, -1.5);
        double tDepthEnd = Mth.clampedMap(maxY, -64, 320, 1.5, -1.5);

        float nStart = (float) Mth.lerp(fStartFactor, tDepthStart, tDepthEnd);
        float nEnd = (float) Mth.lerp(fEndFactor, tDepthStart, tDepthEnd);

        return Climate.Parameter.span(nStart, nEnd);
    }

    @Override
    protected Codec<? extends LazyMultiverseBiomeSource> codec() {
        return BiomeSourceRegistry.LAZY_MULTIVERSE.get();
    }

}
