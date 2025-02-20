package io.github.davidqf555.minecraft.multiverse.common.util;

import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.VanillaMultiverseBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public final class MultiverseConfig {

    private static MultiverseBiomes biomes = VanillaMultiverseBiomes.INSTANCE;
    private static List<ResourceKey<Level>> dimensions = new ArrayList<>();

    private MultiverseConfig() {
    }

    public static MultiverseBiomes getBiomesManager() {
        return biomes;
    }

    public static void setBiomesManager(MultiverseBiomes biomes) {
        MultiverseConfig.biomes = biomes;
    }

    public static List<ResourceKey<Level>> getTargetDimensions() {
        return dimensions;
    }

    public static void setTargetDimensions(List<ResourceKey<Level>> dimensions) {
        MultiverseConfig.dimensions = dimensions;
    }

}
