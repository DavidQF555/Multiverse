package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.*;
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
import java.util.Map;
import java.util.function.Function;

public enum MultiverseShape {

    NORMAL(1, "normal", false, true, -64, 384, 1, 2, 0, provider -> NoiseRouterData.overworld(provider.lookup(Registries.DENSITY_FUNCTION), provider.lookup(Registries.NOISE), false, false), new WeightedSeaLevelSelector(Map.of(
            FlatSeaLevelSelector.of(26, 100), 3,
            new WaveSeaLevelSelector(IntRange.of(45, 53), IntRange.of(10, 16), IntRange.of(48, 64)), 1
    ))),
    ISLANDS(1, "islands", false, false, 0, 256, 2, 1, 0.1f, provider -> NoiseRouterData.floatingIslands(provider.lookup(Registries.DENSITY_FUNCTION), provider.lookup(Registries.NOISE)), FlatSeaLevelSelector.of(-64, -64)),
    ROOFED(1, "roofed", true, true, 0, 128, 1, 2, 0.2f, provider -> NoiseRouterData.nether(provider.lookup(Registries.DENSITY_FUNCTION), provider.lookup(Registries.NOISE)), new WeightedSeaLevelSelector(Map.of(
            FlatSeaLevelSelector.of(24, 40), 3,
            new WaveSeaLevelSelector(IntRange.of(20, 30), IntRange.of(4, 8), IntRange.of(48, 64)), 1
    )));

    private final String name;
    private final NoiseSettings noise;
    private final SeaLevelSelector sea;
    private final Function<BootstapContext<NoiseGeneratorSettings>, NoiseRouter> router;
    private final float light;
    private final boolean floor, ceiling;
    private final int height, weight, minY;

    MultiverseShape(int weight, String name, boolean ceiling, boolean floor, int minY, int height, int sizeHorizontal, int sizeVertical, float light, Function<BootstapContext<NoiseGeneratorSettings>, NoiseRouter> router, SeaLevelSelector sea) {
        this.weight = weight;
        this.minY = minY;
        this.name = name;
        this.floor = floor;
        this.ceiling = ceiling;
        this.height = height;
        this.light = light;
        this.sea = sea;
        this.router = router;
        noise = NoiseSettings.create(this.minY, this.height, sizeHorizontal, sizeVertical);
    }

    public int getMinY() {
        return minY;
    }

    public ResourceKey<NoiseGeneratorSettings> getNoiseSettingsKey(MultiverseType type) {
        return ResourceKey.create(Registries.NOISE_SETTINGS, new ResourceLocation(Multiverse.MOD_ID, type.getName() + "/" + name));
    }

    public NoiseGeneratorSettings createNoiseSettings(BootstapContext<NoiseGeneratorSettings> provider, MultiverseType type) {
        return new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(), router.apply(provider), SurfaceRules.state(Blocks.AIR.defaultBlockState()), List.of(), 0, false, true, true, false);
    }

    public ResourceKey<DimensionType> getTypeKey(MultiverseType type, MultiverseTimeType time, MultiverseEffectType effect) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(Multiverse.MOD_ID, type.getName() + "/" + name + "/" + time.getName() + "/" + effect.getLocation().getPath()));
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

    public Aquifer.FluidPicker getSea(BlockState block, long seed, int index) {
        return sea.getSeaLevel(block, seed, index);
    }

}
