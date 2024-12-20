package io.github.davidqf555.minecraft.multiverse.client.colors;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;

import java.nio.charset.StandardCharsets;
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

    public static int getColor(Level world, ResourceKey<Level> dim) {
        return getColor(getSeed(world.getBiomeManager().biomeZoomSeed, dim));
    }

    public static int getColor(Level level) {
        return getColor(level, level.dimension());
    }

    private static long getSeed(long base, ResourceKey<Level> dim) {
        byte[] arr = (dim.location().getNamespace() + dim.location().getPath()).getBytes(StandardCharsets.UTF_8);
        for (byte b : arr) {
            base += b * 555555;
        }
        return base;
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
