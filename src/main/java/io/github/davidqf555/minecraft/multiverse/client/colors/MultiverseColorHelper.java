package io.github.davidqf555.minecraft.multiverse.client.colors;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;

import java.util.Random;

public final class MultiverseColorHelper {

    private static final long FACTOR = 55555;
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
        return getColors(getSeed(world.getBiomeManager().biomeZoomSeed, dim), n);
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
        return base + ServerConfigs.INSTANCE.colorSeedOffset.get();
    }

    private static int[] getColors(long seed, int n) {
        RANDOM.setSeed(seed);
        return getColors(RANDOM, n);
    }

}
