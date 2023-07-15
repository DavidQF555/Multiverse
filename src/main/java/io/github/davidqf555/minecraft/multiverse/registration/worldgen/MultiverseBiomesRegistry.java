package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.VanillaMultiverseBiomes;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;

import java.util.HashSet;
import java.util.Set;

public final class MultiverseBiomesRegistry {

    private static final Set<TagKey<Biome>> TAGS = new HashSet<>();
    private static MultiverseBiomes biomes = VanillaMultiverseBiomes.INSTANCE;

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

    public static MultiverseBiomes getMultiverseBiomes() {
        return biomes;
    }

    public static void setMultiverseBiomes(MultiverseBiomes biomes) {
        MultiverseBiomesRegistry.biomes = biomes;
    }

    public static void add(TagKey<Biome> tag) {
        TAGS.add(tag);
    }

    public static Set<TagKey<Biome>> getMultiverseBiomeTags() {
        return TAGS;
    }

}
