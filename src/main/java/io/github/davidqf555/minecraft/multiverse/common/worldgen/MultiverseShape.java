package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.dimension_types.effects.MultiverseEffectType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.dimension_types.time.MultiverseTimeType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.FlatSeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.IntRange;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.SeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public enum MultiverseShape {

    NORMAL(1, "normal", false, true, -64, 384, 1, 2, 0, provider -> NoiseRouterData.overworld(provider.lookup(Registries.DENSITY_FUNCTION), provider.lookup(Registries.NOISE), false, false), Optional.empty()),
    ISLANDS(1, "islands", false, false, 0, 256, 2, 1, 0.1f, provider -> NoiseRouterData.floatingIslands(provider.lookup(Registries.DENSITY_FUNCTION), provider.lookup(Registries.NOISE)), Optional.empty()),
    ROOFED(1, "roofed", true, true, 0, 128, 1, 2, 0.2f, provider -> NoiseRouterData.nether(provider.lookup(Registries.DENSITY_FUNCTION), provider.lookup(Registries.NOISE)), Optional.of(MultiverseTimeType.NIGHT));

    private final String name;
    private final NoiseSettings noise;
    private final Function<BootstapContext<NoiseGeneratorSettings>, NoiseRouter> router;
    private final float light;
    private final boolean floor, ceiling;
    private final int height, weight, minY;
    private final Optional<MultiverseTimeType> fixedTime;
    private SeaLevelSelector sea = new FlatSeaLevelSelector(IntRange.of(0, 0));

    MultiverseShape(int weight, String name, boolean ceiling, boolean floor, int minY, int height, int sizeHorizontal, int sizeVertical, float light, Function<BootstapContext<NoiseGeneratorSettings>, NoiseRouter> router, Optional<MultiverseTimeType> fixedTime) {
        this.weight = weight;
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

    public int getMinY() {
        return minY;
    }

    public String getName() {
        return name;
    }

    public ResourceKey<NoiseGeneratorSettings> getNoiseSettingsKey(MultiverseType type) {
        return ResourceKey.create(Registries.NOISE_SETTINGS, new ResourceLocation(Multiverse.MOD_ID, type.getName() + "/" + name));
    }

    public NoiseGeneratorSettings createNoiseSettings(BootstapContext<NoiseGeneratorSettings> provider, MultiverseType type) {
        return new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(), router.apply(provider), SurfaceRules.state(Blocks.AIR.defaultBlockState()), List.of(), 0, false, true, true, false);
    }

    public ResourceKey<DimensionType> getTypeKey(MultiverseType type, MultiverseTimeType time, MultiverseEffectType effect) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(Multiverse.MOD_ID, type.getName() + "/" + name + "/" + time.getName() + "/" + effect.getName()));
    }

    public DimensionType createDimensionType(MultiverseType type, MultiverseTimeType time, MultiverseEffectType effect) {
        return new DimensionType(time.getTime(), !hasCeiling(), hasCeiling(), type.isUltrawarm(), type.isNatural(), 1, true, true, getMinY(), getHeight(), getHeight(), type.getInfiniburn(), effect.getLocation(), light, new DimensionType.MonsterSettings(type.isPiglinSafe(), type.hasRaids(), UniformInt.of(0, 7), 0));
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

    public int getWeight() {
        return weight;
    }

    public SerializableFluidPicker getSea(BlockState block, long seed, int index) {
        return sea.getSeaLevel(block, seed, index);
    }

    public void setSeaLevelSelector(SeaLevelSelector sea) {
        this.sea = sea;
    }

    public Optional<MultiverseTimeType> getFixedTime() {
        return fixedTime;
    }

}
