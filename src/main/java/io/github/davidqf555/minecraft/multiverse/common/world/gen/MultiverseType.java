package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import io.github.davidqf555.minecraft.multiverse.common.registration.worldgen.NoiseSettingsRegistry;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;

public enum MultiverseType {

    NORMAL(1, NoiseSettingsRegistry.NORMAL, false, true, -64, 384, new NoiseSamplingSettings(1, 1, 80, 160), new NoiseSlider(-0.078125D, 2, 8), new NoiseSlider(0.1171875, 3, 0), 1, 2, TerrainProvider.overworld(false), 63, Biomes.PLAINS),
    ISLANDS(1, NoiseSettingsRegistry.ISLANDS, false, false, 0, 256, new NoiseSamplingSettings(2, 1, 80, 160), new NoiseSlider(-23.4375, 64, -46), new NoiseSlider(-0.234375, 7, 1), 2, 1, TerrainProvider.floatingIslands(), -64, Biomes.PLAINS),
    ROOFED(1, NoiseSettingsRegistry.ROOFED, true, true, 0, 128, new NoiseSamplingSettings(1, 3, 80, 60), new NoiseSlider(0.9375, 3, 0), new NoiseSlider(2.5, 4, -1), 1, 2, TerrainProvider.nether(), 32, Biomes.PLAINS);

    private final ResourceKey<NoiseGeneratorSettings> key;
    private final NoiseGeneratorSettings settings;
    private final boolean floor, ceiling;
    private final int height, weight, minY;
    private final ResourceKey<Biome> base;

    MultiverseType(int weight, ResourceKey<NoiseGeneratorSettings> key, boolean ceiling, boolean floor, int minY, int height, NoiseSamplingSettings sampling, NoiseSlider top, NoiseSlider bottom, int sizeHorizontal, int sizeVertical, TerrainShaper terrain, int sea, ResourceKey<Biome> base) {
        this.weight = weight;
        this.minY = minY;
        this.key = key;
        this.floor = floor;
        this.ceiling = ceiling;
        this.height = height;
        this.base = base;
        NoiseSettings noise = NoiseSettings.create(this.minY, this.height, sampling, top, bottom, sizeHorizontal, sizeVertical, terrain);
        this.settings = new NoiseGeneratorSettings(noise, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), !ceiling && floor ? NoiseRouterData.overworldWithNewCaves(noise, false) : NoiseRouterData.nether(noise), MultiverseSurfaceRuleData.combined(ceiling, floor), sea, false, true, true, false);
    }

    public int getMinY() {
        return minY;
    }

    public ResourceKey<NoiseGeneratorSettings> getNoiseSettingsKey() {
        return key;
    }

    public NoiseGeneratorSettings getNoiseSettings() {
        return settings;
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

    public ResourceKey<Biome> getDefaultBiome() {
        return base;
    }

    public int getWeight() {
        return weight;
    }

}
