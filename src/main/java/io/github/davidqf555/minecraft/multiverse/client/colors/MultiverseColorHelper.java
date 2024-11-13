package io.github.davidqf555.minecraft.multiverse.client.colors;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;

import java.util.Random;

public final class MultiverseColorHelper {

    private static final Random RANDOM = new Random(0);

    private MultiverseColorHelper() {
    }

    private static int getColor(Random rand) {
        int[] color = new int[]{rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)};
        shift(color, rand);
        return FastColor.ARGB32.color(0xFF, color[0], color[1], color[2]);
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

    private static void shift(int[] color, Random rand) {
        int i = rand.nextInt(color.length);
        color[i] = color[i] < 0x80 ? 0x00 : 0xFF;
    }

}
