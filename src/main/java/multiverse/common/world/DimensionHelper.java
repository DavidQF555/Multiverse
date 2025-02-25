package multiverse.common.world;

import net.minecraft.resources.ResourceLocation;

public final class DimensionHelper {

    private DimensionHelper() {
    }

    public static long resourceLocationToSeed(ResourceLocation dir, long base, long factor) {
        String loc = dir.getNamespace();
        String path = dir.getPath();
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
            base += factor * c * (i + j);
        }
        return base;
    }

}
