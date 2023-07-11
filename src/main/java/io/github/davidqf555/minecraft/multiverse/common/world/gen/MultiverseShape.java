package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.levelgen.*;

import javax.annotation.Nullable;

public enum MultiverseShape {

    NORMAL(1, "normal", false, true, -64, 384, new NoiseSamplingSettings(1, 1, 80, 160), new NoiseSlider(-0.078125D, 2, 8), new NoiseSlider(0.1171875, 3, 0), 1, 2, TerrainProvider.overworld(false), 63),
    ISLANDS(1, "islands", false, false, 0, 256, new NoiseSamplingSettings(2, 1, 80, 160), new NoiseSlider(-23.4375, 64, -46), new NoiseSlider(-0.234375, 7, 1), 2, 1, TerrainProvider.floatingIslands(), -64),
    ROOFED(1, "roofed", true, true, 0, 128, new NoiseSamplingSettings(1, 3, 80, 60), new NoiseSlider(0.9375, 3, 0), new NoiseSlider(2.5, 4, -1), 1, 2, TerrainProvider.nether(), 32);

    private final String name;
    private final NoiseSettings noise;
    private final boolean floor, ceiling;
    private final int height, weight, minY, sea;

    MultiverseShape(int weight, String name, boolean ceiling, boolean floor, int minY, int height, NoiseSamplingSettings sampling, NoiseSlider top, NoiseSlider bottom, int sizeHorizontal, int sizeVertical, TerrainShaper terrain, int sea) {
        this.weight = weight;
        this.minY = minY;
        this.name = name;
        this.floor = floor;
        this.ceiling = ceiling;
        this.height = height;
        this.sea = sea;
        noise = NoiseSettings.create(this.minY, this.height, sampling, top, bottom, sizeHorizontal, sizeVertical, terrain);
    }

    public int getMinY() {
        return minY;
    }

    public ResourceKey<NoiseGeneratorSettings> getNoiseSettingsKey(@Nullable MultiverseType type) {
        if (type == null) {
            type = MultiverseType.OVERWORLD;
        }
        return ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, type.getName() + "/" + name));
    }

    public NoiseGeneratorSettings createNoiseSettings(@Nullable MultiverseType type) {
        SurfaceRules.RuleSource surface = type == null ? MultiverseSurfaceRuleData.combined(hasCeiling(), hasFloor()) : type.createRuleSource(ceiling, floor);
        type = MultiverseType.OVERWORLD;
        return new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(), !hasCeiling() && hasFloor() ? NoiseRouterData.overworldWithNewCaves(noise, false) : NoiseRouterData.nether(noise), surface, sea, false, true, true, false);
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

}
