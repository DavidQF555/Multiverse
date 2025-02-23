package io.github.davidqf555.minecraft.multiverse.client.colors;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.RiftHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;

import java.util.Random;

public final class MultiverseColorHelper {

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

    public static int[] getColors(ResourceKey<Level> dim, int n) {
        return getColors(RiftHelper.resourceLocationToSeed(getBaseSeed() + ServerConfigs.INSTANCE.colorSeedOffset.get(), dim.location()), n);
    }

    private static int[] getColors(long seed, int n) {
        RANDOM.setSeed(seed);
        return getColors(RANDOM, n);
    }

    private static long getBaseSeed() {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener != null) {
            return listener.registryAccess().lookup(Registries.DIMENSION)
                    .flatMap(registry -> registry.get(Level.OVERWORLD))
                    .filter(Holder::isBound)
                    .map(Holder::value)
                    .map(world -> world.getBiomeManager().biomeZoomSeed).orElse(0L);
        }
        return 0;
    }

}
