package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;

import java.util.HashSet;
import java.util.Set;

public final class MultiverseBiomesRegistry {

    private static final Set<TagKey<Biome>> BIOMES = new HashSet<>();
    private static final Set<ResourceKey<Biome>> VANILLA_OVERWORLD = Set.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.DESERT, Biomes.SWAMP, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.RIVER, Biomes.FROZEN_RIVER, Biomes.BEACH, Biomes.SNOWY_BEACH, Biomes.STONY_SHORE, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.MUSHROOM_FIELDS, Biomes.DRIPSTONE_CAVES, Biomes.LUSH_CAVES);
    private static final Set<ResourceKey<Biome>> VANILLA_NETHER = Set.of(Biomes.NETHER_WASTES, Biomes.WARPED_FOREST, Biomes.CRIMSON_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS);
    private static final Set<ResourceKey<Biome>> VANILLA_END = Set.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);

    static {
        add(BiomeTags.IS_BADLANDS);
        add(BiomeTags.IS_BEACH);
        add(BiomeTags.IS_FOREST);
        add(BiomeTags.IS_HILL);
        add(BiomeTags.IS_JUNGLE);
        add(BiomeTags.IS_DEEP_OCEAN);
        add(BiomeTags.IS_MOUNTAIN);
        add(BiomeTags.IS_NETHER);
        add(BiomeTags.IS_OCEAN);
        add(BiomeTags.IS_RIVER);
        add(BiomeTags.IS_TAIGA);
        add(Tags.Biomes.IS_BEACH);
        add(Tags.Biomes.IS_HOT);
        add(Tags.Biomes.IS_HOT_OVERWORLD);
        add(Tags.Biomes.IS_HOT_NETHER);
        add(Tags.Biomes.IS_HOT_END);
        add(Tags.Biomes.IS_COLD);
        add(Tags.Biomes.IS_COLD_OVERWORLD);
        add(Tags.Biomes.IS_COLD_NETHER);
        add(Tags.Biomes.IS_COLD_END);
        add(Tags.Biomes.IS_SPARSE);
        add(Tags.Biomes.IS_SPARSE_OVERWORLD);
        add(Tags.Biomes.IS_SPARSE_NETHER);
        add(Tags.Biomes.IS_SPARSE_END);
        add(Tags.Biomes.IS_DENSE);
        add(Tags.Biomes.IS_DENSE_OVERWORLD);
        add(Tags.Biomes.IS_DENSE_NETHER);
        add(Tags.Biomes.IS_DENSE_END);
        add(Tags.Biomes.IS_WET);
        add(Tags.Biomes.IS_WET_OVERWORLD);
        add(Tags.Biomes.IS_WET_NETHER);
        add(Tags.Biomes.IS_WET_END);
        add(Tags.Biomes.IS_DRY);
        add(Tags.Biomes.IS_DRY_OVERWORLD);
        add(Tags.Biomes.IS_DRY_NETHER);
        add(Tags.Biomes.IS_DRY_END);
        add(Tags.Biomes.IS_SAVANNA);
        add(Tags.Biomes.IS_CONIFEROUS);
        add(Tags.Biomes.IS_SPOOKY);
        add(Tags.Biomes.IS_DEAD);
        add(Tags.Biomes.IS_LUSH);
        add(Tags.Biomes.IS_MUSHROOM);
        add(Tags.Biomes.IS_MAGICAL);
        add(Tags.Biomes.IS_RARE);
        add(Tags.Biomes.IS_PLATEAU);
        add(Tags.Biomes.IS_MODIFIED);
        add(Tags.Biomes.IS_WATER);
        add(Tags.Biomes.IS_PLAINS);
        add(Tags.Biomes.IS_SWAMP);
        add(Tags.Biomes.IS_SANDY);
        add(Tags.Biomes.IS_SNOWY);
        add(Tags.Biomes.IS_WASTELAND);
        add(Tags.Biomes.IS_BEACH);
        add(Tags.Biomes.IS_VOID);
        add(Tags.Biomes.IS_UNDERGROUND);
        add(Tags.Biomes.IS_PEAK);
        add(Tags.Biomes.IS_SLOPE);
        add(Tags.Biomes.IS_OVERWORLD);
        add(Tags.Biomes.IS_END);
    }

    private MultiverseBiomesRegistry() {
    }

    public static Set<ResourceKey<Biome>> getMultiverseOverworldBiomes() {
        return VANILLA_OVERWORLD;
    }

    public static Set<ResourceKey<Biome>> getMultiverseNetherBiomes() {
        return VANILLA_NETHER;
    }

    public static Set<ResourceKey<Biome>> getMultiverseEndBiomes() {
        return VANILLA_END;
    }

    public static void add(TagKey<Biome> tag) {
        BIOMES.add(tag);
    }

    public static Set<TagKey<Biome>> getMultiverseBiomeTags() {
        return BIOMES;
    }

}
