package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.VanillaMultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.*;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;

public enum MultiverseShape {

    NORMAL(1, "normal", false, true, -64, 384, new NoiseSamplingSettings(1, 1, 80, 160), new NoiseSlider(-0.078125D, 2, 8), new NoiseSlider(0.1171875, 3, 0), 1, 2, TerrainProvider.overworld(false), new WeightedSeaLevelSelector(new SeaLevelSelector[]{
            FlatSeaLevelSelector.of(26, 100),
            new WaveSeaLevelSelector(IntRange.of(56, 70), IntRange.of(1, 3), IntRange.of(48, 64))
    }, new int[]{3, 1})),
    ISLANDS(1, "islands", false, false, 0, 256, new NoiseSamplingSettings(2, 1, 80, 160), new NoiseSlider(-23.4375, 64, -46), new NoiseSlider(-0.234375, 7, 1), 2, 1, TerrainProvider.floatingIslands(), FlatSeaLevelSelector.of(-64, -64)),
    ROOFED(1, "roofed", true, true, 0, 128, new NoiseSamplingSettings(1, 3, 80, 60), new NoiseSlider(0.9375, 3, 0), new NoiseSlider(2.5, 4, -1), 1, 2, TerrainProvider.nether(), new WeightedSeaLevelSelector(new SeaLevelSelector[]{
            FlatSeaLevelSelector.of(24, 40),
            new WaveSeaLevelSelector(IntRange.of(24, 40), IntRange.of(1, 3), IntRange.of(48, 64))
    }, new int[]{3, 1}));

    private final String name;
    private final NoiseSettings noise;
    private final SeaLevelSelector sea;
    private final boolean floor, ceiling;
    private final int height, weight, minY;

    MultiverseShape(int weight, String name, boolean ceiling, boolean floor, int minY, int height, NoiseSamplingSettings sampling, NoiseSlider top, NoiseSlider bottom, int sizeHorizontal, int sizeVertical, TerrainShaper terrain, SeaLevelSelector sea) {
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

    public ResourceKey<NoiseGeneratorSettings> getNoiseSettingsKey(MultiverseType type) {
        return ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, type.getName() + "/" + name));
    }

    public NoiseGeneratorSettings createNoiseSettings(MultiverseType type) {
        return new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(), !hasCeiling() && hasFloor() ? NoiseRouterData.overworldWithNewCaves(noise, false) : NoiseRouterData.nether(noise), VanillaMultiverseBiomes.INSTANCE.createSurface(this, type), 0, false, true, true, false);
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
