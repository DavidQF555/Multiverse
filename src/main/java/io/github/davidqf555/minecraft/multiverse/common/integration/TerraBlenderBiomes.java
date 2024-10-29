package io.github.davidqf555.minecraft.multiverse.common.integration;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseSurfaceRuleData;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.VanillaMultiverseBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

import java.util.*;
import java.util.stream.Collectors;

public class TerraBlenderBiomes implements MultiverseBiomes {

    private static final Climate.ParameterPoint ZERO = Climate.parameters(0, 0, 0, 0, 0, 0, 0);
    private final List<SurfaceRules.RuleSource> overworld;
    private final List<SurfaceRules.RuleSource> nether;
    private final Set<ResourceKey<Biome>> overworldBiomes;
    private final Set<ResourceKey<Biome>> netherBiomes;
    private final Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> parameters = new HashMap<>();

    public TerraBlenderBiomes(Registry<Biome> registry) {
        super();
        Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> overworld = getBiomes(registry, RegionType.OVERWORLD);
        overworldBiomes = overworld.keySet();
        this.overworld = getSurface(SurfaceRuleManager.RuleCategory.OVERWORLD);
        Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> nether = getBiomes(registry, RegionType.NETHER);
        netherBiomes = nether.keySet();
        this.nether = getSurface(SurfaceRuleManager.RuleCategory.NETHER);
        parameters.putAll(nether);
        parameters.putAll(overworld);
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
        return VanillaMultiverseBiomes.INSTANCE.getEndBiomes();
    }

    @Override
    public List<Climate.ParameterPoint> getParameters(ResourceKey<Biome> biome) {
        return parameters.getOrDefault(biome, List.of(ZERO));
    }

    @Override
    public SurfaceRules.RuleSource createSurface(boolean floor, boolean ceiling, MultiverseType type) {
        return switch (type) {
            case OVERWORLD -> MultiverseSurfaceRuleData.overworld(floor && !ceiling, ceiling, floor, overworld);
            case NETHER -> MultiverseSurfaceRuleData.nether(ceiling, floor, nether);
            case END -> VanillaMultiverseBiomes.INSTANCE.createSurface(floor, ceiling, type);
        };
    }

}
