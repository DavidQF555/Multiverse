package multiverse.common.world.worldgen.generators;

import multiverse.common.Multiverse;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class GeneratorHelper {

    private GeneratorHelper() {
    }

    public static long getSeed(long overworld, int index) {
        return overworld + 80000L * index;
    }

    public static ResourceLocation getResourceLocation(int index) {
        return ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, String.valueOf(index));
    }

    public static Optional<Integer> getIndex(ResourceLocation loc) {
        if (!loc.getNamespace().equals(Multiverse.MOD_ID)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(loc.getPath()));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

}
