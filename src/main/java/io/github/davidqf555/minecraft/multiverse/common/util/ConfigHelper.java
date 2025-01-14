package io.github.davidqf555.minecraft.multiverse.common.util;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.VanillaMultiverseBiomes;

public final class ConfigHelper {

    private static MultiverseBiomes biomes = VanillaMultiverseBiomes.INSTANCE;

    public static MultiverseBiomes getBiomesManager() {
        return biomes;
    }

    public static void setBiomesManager(MultiverseBiomes biomes) {
        ConfigHelper.biomes = biomes;
    }

    private ConfigHelper() {
    }

}
