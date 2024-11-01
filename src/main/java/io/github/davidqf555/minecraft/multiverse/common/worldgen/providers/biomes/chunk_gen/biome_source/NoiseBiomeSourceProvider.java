package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.biome_source;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.util.ConfigHelper;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeSourceProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class NoiseBiomeSourceProvider implements BiomeSourceProvider<MultiNoiseBiomeSource> {

    public static final Supplier<Codec<NoiseBiomeSourceProvider>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("min_y").forGetter(val -> val.minY),
            Codec.INT.fieldOf("max_y").forGetter(val -> val.maxY)
    ).apply(inst, NoiseBiomeSourceProvider::new)));
    private final int minY, maxY;

    public NoiseBiomeSourceProvider(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public MultiNoiseBiomeSource provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, Set<ResourceKey<Biome>> biomes) {
        return MultiNoiseBiomeSource.createFromList(getBiomeParameters(access, type, biomes));
    }

    protected Climate.ParameterList<Holder<Biome>> getBiomeParameters(RegistryAccess access, MultiverseType type, Set<ResourceKey<Biome>> biomes) {
        MultiverseBiomes ref = ConfigHelper.biomes;
        Registry<Biome> biomeReg = access.registryOrThrow(Registries.BIOME);
        Registry<DimensionType> dimTypeReg = access.registryOrThrow(Registries.DIMENSION_TYPE);
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> out = new ArrayList<>();
        for (ResourceKey<Biome> biome : biomes) {
            Holder<Biome> holder = biomeReg.getHolderOrThrow(biome);
            for (Climate.ParameterPoint orig : ref.getParameters(biome)) {
                Climate.Parameter depth = translateDepth(orig.depth(), dimTypeReg.getOrThrow(type.getNormalType()));
                Climate.ParameterPoint point = new Climate.ParameterPoint(orig.temperature(), orig.humidity(), orig.continentalness(), orig.erosion(), depth, orig.weirdness(), orig.offset());
                out.add(Pair.of(point, holder));
            }
        }
        return new Climate.ParameterList<>(out);
    }

    //needed because depth function has a constant lerp of y from -64 to 320, scaled from 1.5 to -1.5
    private Climate.Parameter translateDepth(Climate.Parameter depth, DimensionType from) {
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
    public Codec<? extends NoiseBiomeSourceProvider> getCodec() {
        return BiomeSourceProviderTypeRegistry.NOISE.get();
    }

}
