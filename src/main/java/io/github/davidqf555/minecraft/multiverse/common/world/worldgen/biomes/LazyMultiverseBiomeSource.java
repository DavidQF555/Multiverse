package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.util.MultiverseConfig;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
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

    private static final long SEED_OFFSET = 5555555555L;
    public static final MapCodec<LazyMultiverseBiomeSource> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RegistryOps.retrieveRegistryLookup(Registries.BIOME).forGetter(source -> source.registry),
            RegistryOps.retrieveRegistryLookup(Registries.DIMENSION_TYPE).forGetter(source -> source.dimType),
            Codec.INT.fieldOf("min_y").forGetter(source -> source.minY),
            Codec.INT.fieldOf("max_y").forGetter(source -> source.maxY),
            MultiverseType.CODEC.fieldOf("multiverse_type").forGetter(source -> source.type),
            RegistryCodecs.homogeneousList(Registries.BIOME, true).fieldOf("biomes").forGetter(source -> source.biomes),
            Codec.LONG.fieldOf("seed").forGetter(source -> source.seed)
    ).apply(inst, LazyMultiverseBiomeSource::new));
    private final HolderLookup.RegistryLookup<Biome> registry;
    private final HolderLookup.RegistryLookup<DimensionType> dimType;
    private final MultiverseType type;
    private final HolderSet<Biome> biomes;
    private final int minY, maxY;
    private final long seed;

    public LazyMultiverseBiomeSource(HolderLookup.RegistryLookup<Biome> registry, HolderLookup.RegistryLookup<DimensionType> dimType, int minY, int maxY, MultiverseType type, HolderSet<Biome> biomes, long seed) {
        super(() -> MultiNoiseBiomeSource.createFromList(parameters(registry, dimType, minY, maxY, type, biomes, seed)));
        this.registry = registry;
        this.dimType = dimType;
        this.type = type;
        this.biomes = biomes;
        this.minY = minY;
        this.maxY = maxY;
        this.seed = seed;
    }

    public static Climate.ParameterList<Holder<Biome>> parameters(HolderLookup.RegistryLookup<Biome> registry, HolderLookup.RegistryLookup<DimensionType> dimType, int minY, int maxY, MultiverseType type, HolderSet<Biome> biomes, long seed) {
        MultiverseBiomes ref = MultiverseConfig.getBiomesManager();
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> all = new ArrayList<>();
        RandomSource random = new SingleThreadedRandomSource(0);
        for (Holder<Biome> holder : biomes) {
            holder.unwrapKey().ifPresent(key -> {
                if (ref.is(type, key)) {
                    random.setSeed(DimensionHelper.resourceLocationToSeed(seed, key.location()) + SEED_OFFSET);
                    for (Climate.ParameterPoint orig : ref.getParameters(key, random)) {
                        Climate.Parameter depth = translateDepth(orig.depth(), minY, maxY, dimType.getOrThrow(type.getNormalType()).value());
                        Climate.ParameterPoint point = new Climate.ParameterPoint(orig.temperature(), orig.humidity(), orig.continentalness(), orig.erosion(), depth, orig.weirdness(), orig.offset());
                        all.add(Pair.of(point, holder));
                    }
                }
            });
        }
        if (all.isEmpty()) {
            all.add(Pair.of(Climate.parameters(0, 0, 0, 0, 0, 0, 0), registry.getOrThrow(Biomes.THE_VOID)));
        }
        return new Climate.ParameterList<>(all);
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
    protected MapCodec<? extends LazyMultiverseBiomeSource> codec() {
        return CODEC;
    }

}
