package io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes;

import com.google.common.collect.ImmutableSet;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseSurfaceRuleData;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.*;

public class VanillaMultiverseBiomes implements MultiverseBiomes {

    public static final VanillaMultiverseBiomes INSTANCE = new VanillaMultiverseBiomes();
    private static final Set<ResourceKey<Biome>> OVERWORLD;
    private static final Set<ResourceKey<Biome>> NETHER = Set.of(Biomes.NETHER_WASTES, Biomes.WARPED_FOREST, Biomes.CRIMSON_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS);
    private static final Set<ResourceKey<Biome>> END = Set.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);
    private static final Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> PARAMETERS = new HashMap<>();
    private static final Climate.ParameterPoint ZERO = Climate.parameters(0, 0, 0, 0, 0, 0, 0);

    static {
        PARAMETERS.put(Biomes.NETHER_WASTES, List.of(Climate.parameters(0, 0, 0, 0, 0, 0, 0)));
        PARAMETERS.put(Biomes.SOUL_SAND_VALLEY, List.of(Climate.parameters(0, -0.5f, 0, 0, 0, 0, 0)));
        PARAMETERS.put(Biomes.CRIMSON_FOREST, List.of(Climate.parameters(0.4f, 0, 0, 0, 0, 0, 0)));
        PARAMETERS.put(Biomes.WARPED_FOREST, List.of(Climate.parameters(0, 0.5f, 0, 0, 0, 0, 0.375F)));
        PARAMETERS.put(Biomes.BASALT_DELTAS, List.of(Climate.parameters(-0.5f, 0, 0, 0, 0, 0, 0.175F)));
        ImmutableSet.Builder<ResourceKey<Biome>> overworld = ImmutableSet.builder();
        new OverworldBiomeBuilder().addBiomes(pair -> {
            ResourceKey<Biome> key = pair.getSecond();
            overworld.add(key);
            List<Climate.ParameterPoint> add;
            if (PARAMETERS.containsKey(key)) {
                add = PARAMETERS.get(key);
            } else {
                add = new ArrayList<>();
                PARAMETERS.put(key, add);
            }
            add.add(pair.getFirst());
        });
        OVERWORLD = overworld.build();
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
    public List<Climate.ParameterPoint> getParameters(ResourceKey<Biome> biome) {
        return PARAMETERS.getOrDefault(biome, List.of(ZERO));
    }

    @Override
    public SurfaceRules.RuleSource createSurface(boolean floor, boolean ceiling, MultiverseType type) {
        return switch (type) {
            case OVERWORLD ->
                    MultiverseSurfaceRuleData.overworld(floor && !ceiling, ceiling, floor, Collections.emptySet());
            case NETHER -> MultiverseSurfaceRuleData.nether(ceiling, floor, Collections.emptySet());
            case END -> MultiverseSurfaceRuleData.end(ceiling, floor, Collections.emptySet());
        };
    }

}
