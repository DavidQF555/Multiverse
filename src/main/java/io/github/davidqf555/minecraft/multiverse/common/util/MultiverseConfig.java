package io.github.davidqf555.minecraft.multiverse.common.util;

import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.VanillaMultiverseBiomes;

public final class MultiverseConfig {

    private static MultiverseBiomes biomes = VanillaMultiverseBiomes.INSTANCE;

    private MultiverseConfig() {
    }

    public static MultiverseBiomes getBiomesManager() {
        return biomes;
    }

    public static void setBiomesManager(MultiverseBiomes biomes) {
        MultiverseConfig.biomes = biomes;
    }

}
