package io.github.davidqf555.minecraft.multiverse.common.util;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class DimensionHelper {

    private DimensionHelper() {
    }

    @SuppressWarnings("deprecation")
    public static Optional<ServerLevel> getWorld(MinecraftServer server, int index) {
        if (index <= 0) {
            return Optional.of(server.overworld());
        }
        ResourceKey<Level> world = getRegistryKey(Registry.DIMENSION_REGISTRY, index);
        return Optional.ofNullable(server.forgeGetWorldMap().get(world));
    }

    public static long getSeed(long overworld, int index, boolean obfuscated) {
        if (!obfuscated) {
            overworld = BiomeManager.obfuscateSeed(overworld);
        }
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

    public static <T> ResourceKey<T> getRegistryKey(ResourceKey<Registry<T>> registry, int index) {
        return ResourceKey.create(registry, new ResourceLocation(Multiverse.MOD_ID, String.valueOf(index)));
    }

    public static int getIndex(ResourceKey<Level> world) {
        if (world.location().getNamespace().equals(Multiverse.MOD_ID)) {
            return Integer.parseInt(world.location().getPath());
        }
        return 0;
    }

}
