package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.common.BiomeDictionary;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public enum MultiverseBiomesType {

    OVERWORLD("overworld", Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), (ceiling, floor) -> SurfaceRuleData.overworldLike(false, ceiling, floor), biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.OVERWORLD), BlockTags.INFINIBURN_OVERWORLD),
    NETHER("nether", Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), MultiverseSurfaceRuleData::nether, biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER), BlockTags.INFINIBURN_NETHER),
    END("end", Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), MultiverseSurfaceRuleData::end, biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.END), BlockTags.INFINIBURN_END);

    private final String name;
    private final BlockState block, fluid;
    private final BiFunction<Boolean, Boolean, SurfaceRules.RuleSource> surface;
    private final Predicate<ResourceKey<Biome>> is;
    private final TagKey<Block> infiniburn;

    MultiverseBiomesType(String name, BlockState block, BlockState fluid, BiFunction<Boolean, Boolean, SurfaceRules.RuleSource> surface, Predicate<ResourceKey<Biome>> is, TagKey<Block> infiniburn) {
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
