package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.function.BiFunction;

public enum MultiverseBiomesType {

    OVERWORLD("overworld", Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), (ceiling, floor) -> SurfaceRuleData.overworldLike(false, ceiling, floor)),
    NETHER("nether", Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), MultiverseSurfaceRuleData::nether),
    END("end", Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), MultiverseSurfaceRuleData::end);

    private final String name;
    private final BlockState block, fluid;
    private final BiFunction<Boolean, Boolean, SurfaceRules.RuleSource> surface;

    MultiverseBiomesType(String name, BlockState block, BlockState fluid, BiFunction<Boolean, Boolean, SurfaceRules.RuleSource> surface) {
        this.name = name;
        this.block = block;
        this.fluid = fluid;
        this.surface = surface;
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

}
