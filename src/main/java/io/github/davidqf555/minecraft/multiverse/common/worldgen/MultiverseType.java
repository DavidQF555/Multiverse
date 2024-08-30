package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.ConfigHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseRouterWithOnlyNoises;
import net.minecraft.world.level.levelgen.NoiseSettings;

import javax.annotation.Nullable;
import java.util.function.Function;

public enum MultiverseType {

    OVERWORLD("overworld", true, false, true, false, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), BlockTags.INFINIBURN_OVERWORLD, DimensionType.OVERWORLD_LOCATION, noise -> NoiseRouterData.overworldWithNewCaves(noise, false)),
    NETHER("nether", false, true, false, true, Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), BlockTags.INFINIBURN_NETHER, DimensionType.NETHER_LOCATION, NoiseRouterData::nether),
    END("end", false, false, true, false, Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), BlockTags.INFINIBURN_END, DimensionType.END_LOCATION, NoiseRouterData::nether);

    public static final Codec<MultiverseType> CODEC = Codec.STRING.xmap(MultiverseType::byName, MultiverseType::getName);
    private final String name;
    private final BlockState block, fluid;
    private final ResourceKey<DimensionType> normal;
    private final TagKey<Block> infiniburn;
    private final boolean natural, ultrawarm, hasRaids, piglinSafe;
    private final Function<NoiseSettings, NoiseRouterWithOnlyNoises> router;

    MultiverseType(String name, boolean natural, boolean ultrawarm, boolean hasRaids, boolean piglinSafe, BlockState block, BlockState fluid, TagKey<Block> infiniburn, ResourceKey<DimensionType> normal, Function<NoiseSettings, NoiseRouterWithOnlyNoises> router) {
        this.name = name;
        this.block = block;
        this.fluid = fluid;
        this.infiniburn = infiniburn;
        this.natural = natural;
        this.ultrawarm = ultrawarm;
        this.hasRaids = hasRaids;
        this.piglinSafe = piglinSafe;
        this.normal = normal;
        this.router = router;
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

    public boolean hasRaids() {
        return hasRaids;
    }

    public boolean isPiglinSafe() {
        return piglinSafe;
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

    public NoiseRouterWithOnlyNoises createRouter(NoiseSettings noise) {
        return router.apply(noise);
    }

}
