package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.*;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;

import javax.annotation.Nullable;
import java.util.Map;

public enum MultiverseShape {

    NORMAL("normal", false, true, -64, 384, new NoiseSamplingSettings(1, 1, 80, 160), new NoiseSlider(-0.078125D, 2, 8), new NoiseSlider(0.1171875, 3, 0), 1, 2, TerrainProvider.overworld(false), new WeightedSeaLevelSelector(Map.of(
            FlatSeaLevelSelector.of(26, 100), 3,
            new WaveSeaLevelSelector(IntRange.of(45, 53), IntRange.of(10, 16), IntRange.of(48, 64)), 1
    ))),
    ISLANDS("islands", false, false, 0, 256, new NoiseSamplingSettings(2, 1, 80, 160), new NoiseSlider(-23.4375, 64, -46), new NoiseSlider(-0.234375, 7, 1), 2, 1, TerrainProvider.floatingIslands(), FlatSeaLevelSelector.of(-64, -64)),
    ROOFED("roofed", true, true, 0, 128, new NoiseSamplingSettings(1, 3, 80, 60), new NoiseSlider(0.9375, 3, 0), new NoiseSlider(2.5, 4, -1), 1, 2, TerrainProvider.nether(), new WeightedSeaLevelSelector(Map.of(
            FlatSeaLevelSelector.of(24, 40), 3,
            new WaveSeaLevelSelector(IntRange.of(20, 30), IntRange.of(4, 8), IntRange.of(48, 64)), 1
    )));

    private final String name;
    private final NoiseSettings noise;
    private final SeaLevelSelector sea;
    private final boolean floor, ceiling;
    private final int height, minY;

    MultiverseShape(String name, boolean ceiling, boolean floor, int minY, int height, NoiseSamplingSettings sampling, NoiseSlider top, NoiseSlider bottom, int sizeHorizontal, int sizeVertical, TerrainShaper terrain, SeaLevelSelector sea) {
        this.minY = minY;
        this.name = name;
        this.floor = floor;
        this.ceiling = ceiling;
        this.height = height;
        this.sea = sea;
        noise = NoiseSettings.create(this.minY, this.height, sampling, top, bottom, sizeHorizontal, sizeVertical, terrain);
    }

    @Nullable
    public static MultiverseShape byName(String name) {
        for (MultiverseShape shape : values()) {
            if (shape.getName().equals(name)) {
                return shape;
            }
        }
        return null;
    }

    public int getMinY() {
        return minY;
    }

    public ResourceKey<NoiseGeneratorSettings> getNoiseSettingsKey(MultiverseType type) {
        return ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, getLocation(type)));
    }

    public String getLocation(MultiverseType type) {
        return type.getName() + "/" + name;
    }

    public NoiseGeneratorSettings createNoiseSettings(MultiverseType type) {
        return new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(), !hasCeiling() && hasFloor() ? NoiseRouterData.overworldWithNewCaves(noise, false) : NoiseRouterData.nether(noise), SurfaceRules.state(Blocks.AIR.defaultBlockState()), 0, false, true, true, false);
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

    public Aquifer.FluidPicker getSea(BlockState block, long seed, int index) {
        return sea.getSeaLevel(block, seed, index);
    }

    public String getName() {
        return name;
    }

}
