package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import io.github.davidqf555.minecraft.multiverse.registration.worldgen.MultiverseBiomesRegistry;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public enum MultiverseType {

    MIXED("mixed", Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), MultiverseSurfaceRuleData::combined, biome -> MultiverseBiomesRegistry.getMultiverseOverworldBiomes().contains(biome) || MultiverseBiomesRegistry.getMultiverseNetherBiomes().contains(biome) || MultiverseBiomesRegistry.getMultiverseEndBiomes().contains(biome), BlockTags.INFINIBURN_OVERWORLD),
    OVERWORLD("overworld", Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), (ceiling, floor) -> SurfaceRuleData.overworldLike(false, ceiling, floor), biome -> MultiverseBiomesRegistry.getMultiverseOverworldBiomes().contains(biome), BlockTags.INFINIBURN_OVERWORLD),
    NETHER("nether", Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), MultiverseSurfaceRuleData::nether, biome -> MultiverseBiomesRegistry.getMultiverseNetherBiomes().contains(biome), BlockTags.INFINIBURN_NETHER),
    END("end", Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), MultiverseSurfaceRuleData::end, biome -> MultiverseBiomesRegistry.getMultiverseEndBiomes().contains(biome), BlockTags.INFINIBURN_END);

    private final String name;
    private final BlockState block, fluid;
    private final BiFunction<Boolean, Boolean, SurfaceRules.RuleSource> surface;
    private final Predicate<ResourceKey<Biome>> is;
    private final TagKey<Block> infiniburn;

    MultiverseType(String name, BlockState block, BlockState fluid, BiFunction<Boolean, Boolean, SurfaceRules.RuleSource> surface, Predicate<ResourceKey<Biome>> is, TagKey<Block> infiniburn) {
        this.name = name;
        this.block = block;
        this.fluid = fluid;
        this.surface = surface;
        this.is = is;
        this.infiniburn = infiniburn;
    }

    public String getName() {
        return name;
    }

    public BlockState getDefaultBlock() {
        return block;
    }

    public BlockState getDefaultFluid() {
        return fluid;
    }

    public SurfaceRules.RuleSource createRuleSource(boolean ceiling, boolean floor) {
        return surface.apply(ceiling, floor);
    }

    public boolean is(ResourceKey<Biome> biome) {
        return is.test(biome);
    }

    public TagKey<Block> getInfiniburn() {
        return infiniburn;
    }

}
