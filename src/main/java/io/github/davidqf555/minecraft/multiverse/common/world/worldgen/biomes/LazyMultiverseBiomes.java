package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes;

import com.google.common.base.Suppliers;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class LazyMultiverseBiomes implements MultiverseBiomes {

    private final Supplier<MultiverseBiomes> biomes;

    public LazyMultiverseBiomes(Supplier<MultiverseBiomes> biomes) {
        this.biomes = Suppliers.memoize(biomes::get);
    }

    @Override
    public Set<ResourceKey<Biome>> getOverworldBiomes() {
        return biomes.get().getOverworldBiomes();
    }

    @Override
    public Set<ResourceKey<Biome>> getNetherBiomes() {
        return biomes.get().getNetherBiomes();
    }

    @Override
    public Set<ResourceKey<Biome>> getEndBiomes() {
        return biomes.get().getEndBiomes();
    }

    @Override
    public List<Climate.ParameterPoint> getParameters(ResourceKey<Biome> biome, RandomSource random) {
        return biomes.get().getParameters(biome, random);
    }

    @Override
    public SurfaceRules.RuleSource createSurface(boolean floor, boolean ceiling, MultiverseType type) {
        return biomes.get().createSurface(floor, ceiling, type);
    }

}
