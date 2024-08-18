package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.VanillaMultiverseBiomes;

public final class ConfigHelper {

    public static MultiverseBiomes biomes = VanillaMultiverseBiomes.INSTANCE;

    private ConfigHelper() {
    }

}
