package multiverse.common.world.worldgen.biomes;

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public abstract class LazyBiomeSource extends BiomeSource {

    private final Supplier<BiomeSource> source;

    protected LazyBiomeSource(Supplier<BiomeSource> source) {
        super(List.of());
        this.source = Suppliers.memoize(source::get);
        featuresPerStep = Suppliers.memoize(() -> buildFeaturesPerStep(List.copyOf(possibleBiomes()), true));
    }

    @Override
    public Set<Holder<Biome>> possibleBiomes() {
        return source.get().possibleBiomes();
    }

    @Override
    public List<StepFeatureData> featuresPerStep() {
        return source.get().featuresPerStep();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler) {
        return source.get().getNoiseBiome(pX, pY, pZ, pSampler);
    }

}
