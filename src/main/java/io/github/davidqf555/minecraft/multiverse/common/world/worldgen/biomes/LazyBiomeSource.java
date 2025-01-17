package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes;

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class LazyBiomeSource extends BiomeSource {

    private final Supplier<BiomeSource> source;

    protected LazyBiomeSource(Supplier<BiomeSource> source) {
        super();
        this.source = Suppliers.memoize(source::get);
    }

    @Override
    public Stream<Holder<Biome>> collectPossibleBiomes() {
        return source.get().collectPossibleBiomes();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler) {
        return source.get().getNoiseBiome(pX, pY, pZ, pSampler);
    }

}
