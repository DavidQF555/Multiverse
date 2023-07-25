package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import io.github.davidqf555.minecraft.multiverse.registration.worldgen.MultiverseBiomesRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public enum MultiverseType {

    OVERWORLD("overworld", true, false, true, false, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), BlockTags.INFINIBURN_OVERWORLD),
    NETHER("nether", false, true, false, true, Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), BlockTags.INFINIBURN_NETHER),
    END("end", false, false, true, false, Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), BlockTags.INFINIBURN_END);

    private final String name;
    private final BlockState block, fluid;
    private final TagKey<Block> infiniburn;
    private final boolean natural, ultrawarm, hasRaids, piglinSafe;

    MultiverseType(String name, boolean natural, boolean ultrawarm, boolean hasRaids, boolean piglinSafe, BlockState block, BlockState fluid, TagKey<Block> infiniburn) {
        this.name = name;
        this.block = block;
        this.fluid = fluid;
        this.infiniburn = infiniburn;
        this.natural = natural;
        this.ultrawarm = ultrawarm;
        this.hasRaids = hasRaids;
        this.piglinSafe = piglinSafe;
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

    public boolean isNatural() {
        return natural;
    }

    public boolean isUltrawarm() {
        return ultrawarm;
    }

    public boolean hasRaids() {
        return hasRaids;
    }

    public boolean isPiglinSafe() {
        return piglinSafe;
    }

    public boolean is(ResourceKey<Biome> biome) {
        return MultiverseBiomesRegistry.getMultiverseBiomes().getBiomes(this).contains(biome);
    }

    public TagKey<Block> getInfiniburn() {
        return infiniburn;
    }

}
