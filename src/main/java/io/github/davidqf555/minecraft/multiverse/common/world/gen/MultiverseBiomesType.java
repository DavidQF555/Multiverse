package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.common.BiomeDictionary;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public enum MultiverseBiomesType {

    OVERWORLD("overworld", Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), (ceiling, floor) -> SurfaceRuleData.overworldLike(false, ceiling, floor), biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.OVERWORLD)),
    NETHER("nether", Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), MultiverseSurfaceRuleData::nether, biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)),
    END("end", Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), MultiverseSurfaceRuleData::end, biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.END));

    private final String name;
    private final BlockState block, fluid;
    private final BiFunction<Boolean, Boolean, SurfaceRules.RuleSource> surface;
    private final Predicate<ResourceKey<Biome>> is;

    MultiverseBiomesType(String name, BlockState block, BlockState fluid, BiFunction<Boolean, Boolean, SurfaceRules.RuleSource> surface, Predicate<ResourceKey<Biome>> is) {
        this.name = name;
        this.block = block;
        this.fluid = fluid;
        this.surface = surface;
        this.is = is;
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

}
