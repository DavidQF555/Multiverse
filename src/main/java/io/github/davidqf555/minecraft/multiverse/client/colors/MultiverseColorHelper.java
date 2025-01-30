package io.github.davidqf555.minecraft.multiverse.client.colors;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
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
            colors[i] = ARGB.color(0xFF, color[0], color[1], color[2]);
        }
        return colors;
    }

    public static int[] getColors(ResourceKey<Level> dim, int n) {
        return getColors(DimensionHelper.resourceLocationToSeed(getBaseSeed(), dim.location()) + ServerConfigs.INSTANCE.colorSeedOffset.get(), n);
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
