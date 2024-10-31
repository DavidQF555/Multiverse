package io.github.davidqf555.minecraft.multiverse.client.colors;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Random;

public final class MultiverseColorHelper {

    private static final Random RANDOM = new Random(0);

    private MultiverseColorHelper() {
    }

    private static int getColor(Random rand) {
        int[] color = new int[]{rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)};
        applyTransformations(color, rand);
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

    private static void applyTransformations(int[] color, Random rand) {
        shift(color, rand);
        maximizeSaturation(color);
    }

    private static void shift(int[] color, Random rand) {
        int i = rand.nextInt(color.length);
        color[i] = color[i] < 0x80 ? 0xFF : 0x00;
    }

    private static void maximizeSaturation(int[] color) {
        Arrays.stream(color).max().ifPresent(max -> {
            if (max == 0x00) {
                Arrays.fill(color, 0xFF);
            } else {
                for (int i = 0; i < color.length; i++) {
                    color[i] = color[i] * 0xFF / max;
                }
            }
        });
    }

}
