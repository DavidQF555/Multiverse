package multiverse.client.colors;

import multiverse.common.ServerConfigs;
import multiverse.common.world.DimensionHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;

import java.util.Random;

public final class MultiverseColorHelper {

    private static final long FACTOR = 55555L;
    private static final Random RANDOM = new Random(0);
    private static long baseSeed;

    private MultiverseColorHelper() {
    }

    public static void setBaseSeed(long seed) {
        baseSeed = seed;
    }

    public static int[] getColors(Random rand, int n) {
        int[] colors = new int[n];
        int[] bounds = new int[]{0, 1, 2};
        for (int i = 2; i >= 1; i--) {
            int j = rand.nextInt(i + 1);
            int temp = bounds[i];
            bounds[i] = bounds[j];
            bounds[j] = temp;
        }
        boolean side1 = rand.nextBoolean();
        boolean side2 = rand.nextBoolean();
        for (int i = 0; i < n; i++) {
            int[] color = new int[3];
            color[bounds[0]] = side1 ? 0x00 : 0xFF;
            color[bounds[1]] = rand.nextInt(128);
            if (side2) {
                color[bounds[1]] = 0xFF - color[bounds[1]];
            }
            color[bounds[2]] = rand.nextInt(256);
            colors[i] = FastColor.ARGB32.color(0xFF, color[0], color[1], color[2]);
        }
        return colors;
    }

    public static int[] getColors(ResourceKey<Level> dim, int n) {
        return getColors(DimensionHelper.resourceLocationToSeed(dim.location(), baseSeed + ServerConfigs.INSTANCE.colorSeedOffset.get(), FACTOR), n);
    }

    private static int[] getColors(long seed, int n) {
        RANDOM.setSeed(seed);
        return getColors(RANDOM, n);
    }

}
