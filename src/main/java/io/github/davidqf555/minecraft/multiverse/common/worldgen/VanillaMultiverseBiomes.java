package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VanillaMultiverseBiomes implements MultiverseBiomes {

    public static final VanillaMultiverseBiomes INSTANCE = new VanillaMultiverseBiomes();
    private static final Set<ResourceKey<Biome>> OVERWORLD;
    private static final Set<ResourceKey<Biome>> NETHER = Set.of(Biomes.NETHER_WASTES, Biomes.WARPED_FOREST, Biomes.CRIMSON_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS);
    private static final Set<ResourceKey<Biome>> END = Set.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);
    private static final Set<ResourceKey<Biome>> MIXED;
    private static final Map<ResourceKey<Biome>, Climate.ParameterPoint> PARAMETERS = new HashMap<>();
    private static final Climate.ParameterPoint ZERO = Climate.parameters(0, 0, 0, 0, 0, 0, 0);

    static {
        PARAMETERS.put(Biomes.NETHER_WASTES, Climate.parameters(0, 0, 0, 0, 0, 0, 0));
        PARAMETERS.put(Biomes.SOUL_SAND_VALLEY, Climate.parameters(0, -0.5f, 0, 0, 0, 0, 0));
        PARAMETERS.put(Biomes.CRIMSON_FOREST, Climate.parameters(0.4f, 0, 0, 0, 0, 0, 0));
        PARAMETERS.put(Biomes.WARPED_FOREST, Climate.parameters(0, 0.5f, 0, 0, 0, 0, 0.375F));
        PARAMETERS.put(Biomes.BASALT_DELTAS, Climate.parameters(-0.5f, 0, 0, 0, 0, 0, 0.175F));
        ImmutableSet.Builder<ResourceKey<Biome>> overworld = ImmutableSet.builder();
        new OverworldBiomeBuilder().addBiomes(pair -> {
            overworld.add(pair.getSecond());
            PARAMETERS.put(pair.getSecond(), pair.getFirst());
        });
        OVERWORLD = overworld.build();
        ImmutableSet.Builder<ResourceKey<Biome>> builder = ImmutableSet.builder();
        builder.addAll(OVERWORLD);
        builder.addAll(NETHER);
        builder.addAll(END);
        MIXED = builder.build();
    }

    protected VanillaMultiverseBiomes() {
    }

    @Override
    public Set<ResourceKey<Biome>> getOverworldBiomes() {
        return OVERWORLD;
    }

    @Override
    public Set<ResourceKey<Biome>> getNetherBiomes() {
        return NETHER;
    }

    @Override
    public Set<ResourceKey<Biome>> getEndBiomes() {
        return END;
    }

    @Override
    public Set<ResourceKey<Biome>> getMixedBiomes() {
        return MIXED;
    }

    @Override
    public Climate.ParameterPoint getParameters(ResourceKey<Biome> biome) {
        return PARAMETERS.getOrDefault(biome, ZERO);
    }

    @Override
    public boolean overrideVanillaSurface() {
        return false;
    }

    @Override
    public SurfaceRules.RuleSource createSurface(MultiverseShape shape, MultiverseType type) {
        boolean ceiling = shape.hasCeiling();
        boolean floor = shape.hasFloor();
        return switch (type) {
            case MIXED ->
                    MultiverseSurfaceRuleData.combined(ceiling, floor, getOverworldBiomes().toArray(ResourceKey[]::new), getNetherBiomes().toArray(ResourceKey[]::new), getEndBiomes().toArray(ResourceKey[]::new), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
            case NETHER -> MultiverseSurfaceRuleData.nether(ceiling, floor, Collections.emptySet());
            case END -> MultiverseSurfaceRuleData.end(ceiling, floor, Collections.emptySet());
            default -> MultiverseSurfaceRuleData.overworld(ceiling, floor, Collections.emptySet());
        };
    }

}
