package io.github.davidqf555.minecraft.multiverse.common.util;

import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.VanillaMultiverseBiomes;

public final class MultiverseConfig {

    private static MultiverseBiomes biomes = VanillaMultiverseBiomes.INSTANCE;

    public static MultiverseBiomes getBiomesManager() {
        return biomes;
    }

    private MultiverseConfig() {
    }

    public static void setBiomesManager(MultiverseBiomes biomes) {
        MultiverseConfig.biomes = biomes;
    }

}
