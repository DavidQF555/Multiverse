package io.github.davidqf555.minecraft.multiverse.common.integration;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseSurfaceRuleData;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.VanillaMultiverseBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.EndBiomeRegistry;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

import java.util.*;
import java.util.stream.Collectors;

public class TerraBlenderBiomes implements MultiverseBiomes {

    private static final List<Climate.ParameterPoint> ZERO = List.of(Climate.parameters(0, 0, 0, 0, 0, 0, 0));
    private final List<SurfaceRules.RuleSource> overworld, nether, end;
    private final Set<ResourceKey<Biome>> overworldBiomes, netherBiomes, endBiomes;
    private final Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> parameters = new HashMap<>();

    public TerraBlenderBiomes(Registry<Biome> registry) {
        super();
        this.overworld = getSurface(SurfaceRuleManager.RuleCategory.OVERWORLD);
        this.nether = getSurface(SurfaceRuleManager.RuleCategory.NETHER);
        this.end = getSurface(SurfaceRuleManager.RuleCategory.END);
        parameters.putAll(VanillaMultiverseBiomes.getParameters());
        Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> overworld = getBiomes(registry, RegionType.OVERWORLD);
        overworldBiomes = overworld.keySet();
        Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> nether = getBiomes(registry, RegionType.NETHER);
        netherBiomes = nether.keySet();
        Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> end = getEndBiomesParameters();
        endBiomes = end.keySet();
        parameters.putAll(nether);
        parameters.putAll(overworld);
        parameters.putAll(end);
    }

    private static Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> getBiomes(Registry<Biome> registry, RegionType type) {
        Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> map = new HashMap<>();
        Regions.get(type).forEach(region -> region.addBiomes(registry, pair -> {
            ResourceKey<Biome> key = pair.getSecond();
            List<Climate.ParameterPoint> add;
            if (map.containsKey(key)) {
                add = map.get(key);
            } else {
                add = new ArrayList<>();
                map.put(key, add);
            }
            add.add(pair.getFirst());
        }));
        return map;
    }

    private static Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> getEndBiomesParameters() {
        Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> map = new HashMap<>();
        for (WeightedEntry.Wrapper<ResourceKey<Biome>> entry : EndBiomeRegistry.getIslandBiomes()) {
            map.put(entry.data(), getEndParameters(-1, -0.21875f));
        }
        for (WeightedEntry.Wrapper<ResourceKey<Biome>> entry : EndBiomeRegistry.getEdgeBiomes()) {
            map.put(entry.data(), getEndParameters(-0.21875f, -0.0625f));
        }
        for (WeightedEntry.Wrapper<ResourceKey<Biome>> entry : EndBiomeRegistry.getMidlandsBiomes()) {
            map.put(entry.data(), getEndParameters(-0.0625f, 0.25f));
        }
        for (WeightedEntry.Wrapper<ResourceKey<Biome>> entry : EndBiomeRegistry.getHighlandsBiomes()) {
            map.put(entry.data(), getEndParameters(0.25f, 1));
        }
        return map;
    }

    private static List<Climate.ParameterPoint> getEndParameters(float min, float max) {
        return List.of(Climate.parameters(
                Climate.Parameter.point(0),
                Climate.Parameter.point(0),
                Climate.Parameter.point(0),
                Climate.Parameter.span(min, max),
                Climate.Parameter.point(0),
                Climate.Parameter.point(0),
                0
        ));
    }

    private static List<SurfaceRules.RuleSource> getSurface(SurfaceRuleManager.RuleCategory category) {
        return ((NamespacedSurfaceRuleSource) SurfaceRuleManager.getNamespacedRules(category, null)).sources().entrySet().stream()
                .filter(entry -> !entry.getKey().equals("minecraft"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Set<ResourceKey<Biome>> getOverworldBiomes() {
        return overworldBiomes;
    }

    @Override
    public Set<ResourceKey<Biome>> getNetherBiomes() {
        return netherBiomes;
    }

    @Override
    public Set<ResourceKey<Biome>> getEndBiomes() {
        return endBiomes;
    }

    private static Climate.ParameterPoint offset(Climate.ParameterPoint base, RandomSource random) {
        float tOffset = (float) (random.nextGaussian() * ServerConfigs.INSTANCE.temperatureScale.get());
        float hOffset = (float) (random.nextGaussian() * ServerConfigs.INSTANCE.humidityScale.get());
        Climate.Parameter temperature = Climate.Parameter.span(
                Mth.clamp(Climate.unquantizeCoord(base.temperature().min()) + tOffset, -2.0f, 2.0f),
                Mth.clamp(Climate.unquantizeCoord(base.temperature().min()) + tOffset, -2.0f, 2.0f)
        );
        Climate.Parameter humidity = Climate.Parameter.span(
                Mth.clamp(Climate.unquantizeCoord(base.humidity().min()) + hOffset, -2.0f, 2.0f),
                Mth.clamp(Climate.unquantizeCoord(base.humidity().min()) + hOffset, -2.0f, 2.0f)
        );
        return new Climate.ParameterPoint(
                temperature,
                humidity,
                base.continentalness(),
                base.erosion(),
                base.depth(),
                base.weirdness(),
                base.offset()
        );
    }

    @Override
    public List<Climate.ParameterPoint> getParameters(ResourceKey<Biome> biome, RandomSource random) {
        List<Climate.ParameterPoint> original = parameters.getOrDefault(biome, ZERO);
        List<Climate.ParameterPoint> offset = new ArrayList<>();
        for (Climate.ParameterPoint base : original) {
            offset.add(offset(base, random));
        }
        return offset;
    }

    @Override
    public SurfaceRules.RuleSource createSurface(boolean floor, boolean ceiling, MultiverseType type) {
        return switch (type) {
            case OVERWORLD -> MultiverseSurfaceRuleData.overworld(floor && !ceiling, ceiling, floor, overworld);
            case NETHER -> MultiverseSurfaceRuleData.nether(ceiling, floor, nether);
            case END -> MultiverseSurfaceRuleData.end(ceiling, floor, end);
        };
    }

}
