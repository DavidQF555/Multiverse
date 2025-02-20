package io.github.davidqf555.minecraft.multiverse.common.world;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.util.MultiverseConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class DimensionHelper {

    private DimensionHelper() {
    }

    public static Optional<ResourceKey<Level>> randomTargetDimension(Random random, @Nullable ResourceKey<Level> exclude) {
        List<ResourceKey<Level>> possible = new ArrayList<>(MultiverseConfig.getTargetDimensions());
        if (exclude != null) {
            possible.remove(exclude);
        }
        if (possible.isEmpty()) {
            return Optional.empty();
        }
        int i = random.nextInt(possible.size());
        return Optional.of(possible.get(i));
    }

    public static long getSeed(long overworld, int index) {
        return overworld + 80000L * index;
    }

    public static Vec3 translate(Vec3 pos, DimensionType from, DimensionType to, boolean logical) {
        int fromHeight = logical ? from.logicalHeight() : from.height();
        int toHeight = logical ? to.logicalHeight() : to.height();
        double factorY = Mth.clamp((pos.y() - from.minY()) / fromHeight, 0, 1);
        double y = to.minY() + toHeight * factorY;
        double scale = DimensionType.getTeleportationScale(from, to);
        return new Vec3(pos.x() * scale, y, pos.z() * scale);
    }

    public static ResourceLocation getResourceLocation(int index) {
        return new ResourceLocation(Multiverse.MOD_ID, String.valueOf(index));
    }

    public static Optional<Integer> getIndex(ResourceKey<Level> world) {
        if (world.equals(Level.OVERWORLD)) {
            return Optional.of(0);
        } else if (world.location().getNamespace().equals(Multiverse.MOD_ID)) {
            try {
                return Optional.of(Integer.parseInt(world.location().getPath()));
            } catch (NumberFormatException ignored) {
            }
        }
        return Optional.empty();
    }

}
