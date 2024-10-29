package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.ConfigHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import javax.annotation.Nullable;

public enum MultiverseType {

    OVERWORLD("overworld", true, false, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)),
    NETHER("nether", false, true, Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), BlockTags.INFINIBURN_NETHER, BuiltinDimensionTypes.NETHER, new DimensionType.MonsterSettings(true, false, ConstantInt.of(7), 15)),
    END("end", false, false, Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), BlockTags.INFINIBURN_END, BuiltinDimensionTypes.END, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0));

    public static final Codec<MultiverseType> CODEC = Codec.STRING.xmap(MultiverseType::byName, MultiverseType::getName);
    private final String name;
    private final BlockState block, fluid;
    private final ResourceKey<DimensionType> normal;
    private final TagKey<Block> infiniburn;
    private final boolean natural, ultrawarm;
    private final DimensionType.MonsterSettings monster;

    MultiverseType(String name, boolean natural, boolean ultrawarm, BlockState block, BlockState fluid, TagKey<Block> infiniburn, ResourceKey<DimensionType> normal, DimensionType.MonsterSettings monster) {
        this.name = name;
        this.block = block;
        this.fluid = fluid;
        this.infiniburn = infiniburn;
        this.natural = natural;
        this.ultrawarm = ultrawarm;
        this.normal = normal;
        this.monster = monster;
    }

    @Nullable
    public static MultiverseType byName(String name) {
        for (MultiverseType type : values()) {
            if (name.equals(type.getName())) {
                return type;
            }
        }
        return null;
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

    public boolean is(ResourceKey<Biome> biome) {
        return ConfigHelper.biomes.getBiomes(this).contains(biome);
    }

    public TagKey<Block> getInfiniburn() {
        return infiniburn;
    }

    public ResourceKey<DimensionType> getNormalType() {
        return normal;
    }

    public DimensionType.MonsterSettings getMonsterSettings() {
        return monster;
    }

}
