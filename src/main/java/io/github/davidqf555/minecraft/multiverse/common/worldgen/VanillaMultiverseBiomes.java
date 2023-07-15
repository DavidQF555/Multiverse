package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Collections;
import java.util.Set;

public class VanillaMultiverseBiomes implements MultiverseBiomes {

    public static final VanillaMultiverseBiomes INSTANCE = new VanillaMultiverseBiomes();
    private static final Set<ResourceKey<Biome>> OVERWORLD = Set.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.DESERT, Biomes.SWAMP, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.RIVER, Biomes.FROZEN_RIVER, Biomes.BEACH, Biomes.SNOWY_BEACH, Biomes.STONY_SHORE, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.MUSHROOM_FIELDS, Biomes.DRIPSTONE_CAVES, Biomes.LUSH_CAVES);
    private static final Set<ResourceKey<Biome>> NETHER = Set.of(Biomes.NETHER_WASTES, Biomes.WARPED_FOREST, Biomes.CRIMSON_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS);
    private static final Set<ResourceKey<Biome>> END = Set.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);
    private static final Set<ResourceKey<Biome>> MIXED;

    static {
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
