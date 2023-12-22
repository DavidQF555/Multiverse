package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.effects.MultiverseEffect;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.FlatSeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.IntRange;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.SeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public enum MultiverseShape {

    NORMAL("normal", false, true, -64, 384, 1, 2, 0, provider -> NoiseRouterData.overworld(provider.lookupOrThrow(Registries.DENSITY_FUNCTION), provider.lookupOrThrow(Registries.NOISE), false, false), Optional.empty()),
    ISLANDS("islands", false, false, 0, 256, 2, 1, 0.1f, provider -> NoiseRouterData.floatingIslands(provider.lookupOrThrow(Registries.DENSITY_FUNCTION), provider.lookupOrThrow(Registries.NOISE)), Optional.empty()),
    ROOFED("roofed", true, true, 0, 128, 1, 2, 0.2f, provider -> NoiseRouterData.nether(provider.lookupOrThrow(Registries.DENSITY_FUNCTION), provider.lookupOrThrow(Registries.NOISE)), Optional.of(MultiverseTime.NIGHT));

    private final String name;
    private final NoiseSettings noise;
    private final Function<HolderLookup.Provider, NoiseRouter> router;
    private final float light;
    private final boolean floor, ceiling;
    private final int height, minY;
    private final Optional<MultiverseTime> fixedTime;
    private SeaLevelSelector sea = new FlatSeaLevelSelector(IntRange.of(0, 0));

    MultiverseShape(String name, boolean ceiling, boolean floor, int minY, int height, int sizeHorizontal, int sizeVertical, float light, Function<HolderLookup.Provider, NoiseRouter> router, Optional<MultiverseTime> fixedTime) {
        this.minY = minY;
        this.name = name;
        this.floor = floor;
        this.ceiling = ceiling;
        this.height = height;
        this.light = light;
        this.router = router;
        this.fixedTime = fixedTime;
        noise = NoiseSettings.create(this.minY, this.height, sizeHorizontal, sizeVertical);
    }

    @Nullable
    public static MultiverseShape byName(String name) {
        for (MultiverseShape type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public int getMinY() {
        return minY;
    }

    public String getName() {
        return name;
    }

    public ResourceKey<NoiseGeneratorSettings> getNoiseSettingsKey(MultiverseType type) {
        return ResourceKey.create(Registries.NOISE_SETTINGS, new ResourceLocation(Multiverse.MOD_ID, type.getName() + "/" + name));
    }

    public NoiseGeneratorSettings createNoiseSettings(HolderLookup.Provider provider, MultiverseType type) {
        return new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(), router.apply(provider), SurfaceRules.state(Blocks.AIR.defaultBlockState()), List.of(), 0, false, true, true, false);
    }

    public ResourceKey<DimensionType> getTypeKey(MultiverseType type, MultiverseTime time, MultiverseEffect effect) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(Multiverse.MOD_ID, type.getName() + "/" + name + "/" + time.getName() + "/" + effect.getName()));
    }

    public DimensionType createDimensionType(MultiverseType type, MultiverseTime time, MultiverseEffect effect) {
        return new DimensionType(time.getTime(), !hasCeiling(), hasCeiling(), type.isUltrawarm(), type.isNatural(), 1, true, true, getMinY(), getHeight(), getHeight(), type.getInfiniburn(), effect.getLocation(), light, type.getMonsterSettings());
    }

    public boolean hasFloor() {
        return floor;
    }

    public boolean hasCeiling() {
        return ceiling;
    }

    public int getHeight() {
        return height;
    }

    public SerializableFluidPicker getSea(BlockState block, long seed, int index) {
        return sea.getSeaLevel(block, seed, index);
    }

    public void setSeaLevelSelector(SeaLevelSelector sea) {
        this.sea = sea;
    }

    public Optional<MultiverseTime> getFixedTime() {
        return fixedTime;
    }

}
