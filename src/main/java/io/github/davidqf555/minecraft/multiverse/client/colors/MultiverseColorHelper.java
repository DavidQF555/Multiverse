package io.github.davidqf555.minecraft.multiverse.client.colors;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;

import java.util.Random;

public final class MultiverseColorHelper {

    private static final Random RANDOM = new Random(0);

    private MultiverseColorHelper() {
    }

    private static int[] getColors(Random rand, int n) {
        int[] colors = new int[n];
        int shift = rand.nextInt(3);
        boolean side = rand.nextBoolean();
        for (int i = 0; i < n; i++) {
            int[] color = new int[3];
            for (int j = 0; j < 3; j++) {
                if (j == shift) {
                    color[j] = side ? 0 : 0xFF;
                } else {
                    color[j] = rand.nextInt(256);
                }
            }
            colors[i] = FastColor.ARGB32.color(0xFF, color[0], color[1], color[2]);
        }
        return colors;
    }

    public static int[] getColors(Level world, ResourceKey<Level> dim, int n) {
        return getColors(DimensionHelper.resourceLocationToSeed(world.getBiomeManager().biomeZoomSeed, dim.location()) + ServerConfigs.INSTANCE.colorSeedOffset.get(), n);
    }

    public static int[] getColors(Level level, int n) {
        return getColors(level, level.dimension(), n);
    }

    private static int[] getColors(long seed, int n) {
        RANDOM.setSeed(seed);
        return getColors(RANDOM, n);
    }

}
