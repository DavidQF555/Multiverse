package io.github.davidqf555.minecraft.multiverse.common.integration;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.*;
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
    private final Set<ResourceKey<Biome>> mixed = new HashSet<>();
    private final Map<ResourceKey<Biome>, Climate.ParameterPoint> parameters = new HashMap<>();

    public TerraBlenderBiomes(Registry<Biome> registry) {
        super();
        Map<ResourceKey<Biome>, Climate.ParameterPoint> overworld = getBiomes(registry, RegionType.OVERWORLD);
        overworldBiomes = overworld.keySet();
        this.overworld = getSurface(SurfaceRuleManager.RuleCategory.OVERWORLD);
        Map<ResourceKey<Biome>, Climate.ParameterPoint> nether = getBiomes(registry, RegionType.NETHER);
        netherBiomes = nether.keySet();
        this.nether = getSurface(SurfaceRuleManager.RuleCategory.NETHER);
        parameters.putAll(overworld);
        parameters.putAll(nether);
        mixed.addAll(overworldBiomes);
        mixed.addAll(netherBiomes);
        mixed.addAll(VanillaMultiverseBiomes.INSTANCE.getEndBiomes());
    }

    private static Map<ResourceKey<Biome>, Climate.ParameterPoint> getBiomes(Registry<Biome> registry, RegionType type) {
        Map<ResourceKey<Biome>, Climate.ParameterPoint> map = new HashMap<>();
        Regions.get(type).forEach(region -> region.addBiomes(registry, pair -> map.put(pair.getSecond(), pair.getFirst())));
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
    public Set<ResourceKey<Biome>> getMixedBiomes() {
        return mixed;
    }

    @Override
    public Climate.ParameterPoint getParameters(ResourceKey<Biome> biome) {
        return parameters.getOrDefault(biome, ZERO);
    }

    @Override
    public SurfaceRules.RuleSource createSurface(MultiverseShape shape, MultiverseType type) {
        boolean ceiling = shape.hasCeiling();
        boolean floor = shape.hasFloor();
        return switch (type) {
            case MIXED ->
                    MultiverseSurfaceRuleData.combined(ceiling, floor, getOverworldBiomes().toArray(ResourceKey[]::new), getNetherBiomes().toArray(ResourceKey[]::new), getEndBiomes().toArray(ResourceKey[]::new), overworld, nether, Collections.emptyList());
            case END -> VanillaMultiverseBiomes.INSTANCE.createSurface(shape, type);
            case NETHER -> MultiverseSurfaceRuleData.nether(ceiling, floor, nether);
            default -> MultiverseSurfaceRuleData.overworld(ceiling, floor, overworld);
        };
    }

}
