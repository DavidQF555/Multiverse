package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.VanillaMultiverseBiomes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;

import java.util.List;

public enum MultiverseShape {

    NORMAL(1, "normal", false, true, -64, 384, 1, 2, 63),
    ISLANDS(1, "islands", false, false, 0, 256, 2, 1, -64),
    ROOFED(1, "roofed", true, true, 0, 128, 1, 2, 32);

    private final String name;
    private final NoiseSettings noise;
    private final boolean floor, ceiling;
    private final int height, weight, minY, sea;

    MultiverseShape(int weight, String name, boolean ceiling, boolean floor, int minY, int height, int sizeHorizontal, int sizeVertical, int sea) {
        this.weight = weight;
        this.minY = minY;
        this.name = name;
        this.floor = floor;
        this.ceiling = ceiling;
        this.height = height;
        this.sea = sea;
        noise = NoiseSettings.create(this.minY, this.height, sizeHorizontal, sizeVertical);
    }

    public int getMinY() {
        return minY;
    }

    public ResourceKey<NoiseGeneratorSettings> getNoiseSettingsKey(MultiverseType type) {
        return ResourceKey.create(Registries.NOISE_SETTINGS, new ResourceLocation(Multiverse.MOD_ID, type.getName() + "/" + name));
    }

    public NoiseGeneratorSettings createNoiseSettings(HolderLookup.Provider provider, MultiverseType type) {
        return new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(), !hasCeiling() && hasFloor() ? NoiseRouterData.overworld(provider.lookupOrThrow(Registries.DENSITY_FUNCTION), provider.lookupOrThrow(Registries.NOISE), false, false) : NoiseRouterData.nether(provider.lookupOrThrow(Registries.DENSITY_FUNCTION), provider.lookupOrThrow(Registries.NOISE)), VanillaMultiverseBiomes.INSTANCE.createSurface(this, type), List.of(), sea, false, true, true, false);
    }

    public ResourceKey<DimensionType> getTypeKey(MultiverseType type, MultiverseTimeType time, MultiverseEffectType effect) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(Multiverse.MOD_ID, type.getName() + "/" + name + "/" + time.getName() + "/" + effect.getName()));
    }

    public DimensionType createDimensionType(MultiverseType type, MultiverseTimeType time, MultiverseEffectType effect) {
        boolean ceiling = hasCeiling();
        return new DimensionType(time.getTime(), !ceiling, ceiling, false, true, 1, true, true, getMinY(), getHeight(), getHeight(), type.getInfiniburn(), effect.getEffect(), 0.1f, new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0));
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
