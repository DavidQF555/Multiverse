package multiverse.common.world.worldgen.generators;

import multiverse.common.Multiverse;
import net.minecraft.resources.ResourceLocation;

public final class GeneratorHelper {

    private GeneratorHelper() {
    }

    public static long getSeed(long overworld, int index) {
        return overworld + 80000L * index;
    }

    public static ResourceLocation getResourceLocation(int index) {
        return new ResourceLocation(Multiverse.MOD_ID, String.valueOf(index));
    }

}
