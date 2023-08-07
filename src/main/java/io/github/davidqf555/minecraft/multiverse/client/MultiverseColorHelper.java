package io.github.davidqf555.minecraft.multiverse.client;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;

import java.util.Random;

public final class MultiverseColorHelper {

    private static final Random RANDOM = new Random(0);

    private MultiverseColorHelper() {
    }

    private static int getColor(Random rand) {
        return FastColor.ARGB32.color(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public static int getColor(Level world, int index) {
        return getColor(DimensionHelper.getSeed(world.getBiomeManager().biomeZoomSeed, index, true));
    }

    public static int getColor(Level level) {
        return getColor(level, DimensionHelper.getIndex(level.dimension()));
    }

    private static int getColor(long seed) {
        RANDOM.setSeed(seed);
        return getColor(RANDOM);
    }

}
