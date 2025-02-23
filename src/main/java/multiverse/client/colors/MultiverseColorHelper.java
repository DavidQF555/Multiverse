package multiverse.client.colors;

import multiverse.common.ServerConfigs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;

import java.util.Random;

public final class MultiverseColorHelper {

    private static final long FACTOR = 55555;
    private static final Random RANDOM = new Random(0);

    private MultiverseColorHelper() {
    }

    public static int[] getColors(Random rand, int n) {
        int[] colors = new int[n];
        int fixed = rand.nextInt(3);
        int half = rand.nextInt(2);
        if (half >= fixed) {
            half++;
        }
        int free = 0;
        if (fixed != 1 && half != 1) {
            free = 1;
        } else if (fixed != 2 && half != 2) {
            free = 2;
        }
        boolean side1 = rand.nextBoolean();
        boolean side2 = rand.nextBoolean();
        for (int i = 0; i < n; i++) {
            int[] color = new int[3];
            color[fixed] = side1 ? 0x00 : 0xFF;
            color[half] = rand.nextInt(128);
            if (side2) {
                color[half] = 0xFF - color[half];
            }
            color[free] = rand.nextInt(256);
            colors[i] = FastColor.ARGB32.color(0xFF, color[0], color[1], color[2]);
        }
        return colors;
    }

    public static int[] getColors(Level world, ResourceKey<Level> dim, int n) {
        return getColors(getSeed(world.getBiomeManager().biomeZoomSeed + ServerConfigs.INSTANCE.colorSeedOffset.get(), dim), n);
    }

    public static int[] getColors(Level level, int n) {
        return getColors(level, level.dimension(), n);
    }

    private static long getSeed(long base, ResourceKey<Level> dim) {
        String loc = dim.location().getNamespace();
        String path = dim.location().getPath();
        int i = 0;
        int j = 0;
        while (i < loc.length() || j < path.length()) {
            char c;
            if (i >= loc.length()) {
                c = path.charAt(j++);
            } else if (j >= path.length()) {
                c = loc.charAt(i++);
            } else if ((i + j) % 2 == 0) {
                c = path.charAt(j++);
            } else {
                c = loc.charAt(i++);
            }
            base += FACTOR * c * (i + j);
        }
        return base;
    }

    private static int[] getColors(long seed, int n) {
        RANDOM.setSeed(seed);
        return getColors(RANDOM, n);
    }

}
