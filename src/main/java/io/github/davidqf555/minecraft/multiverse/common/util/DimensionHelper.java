package io.github.davidqf555.minecraft.multiverse.common.util;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class DimensionHelper {

    private DimensionHelper() {
    }

    public static ResourceKey<Level> randomMultiverseDimension(RandomSource random, Optional<ResourceKey<Level>> exclude) {
        Optional<Integer> multiverse = exclude.flatMap(DimensionHelper::getIndex);
        int size = ServerConfigs.INSTANCE.maxDimensions.get() + 1;
        if (multiverse.isPresent()) {
            size--;
        }
        int rand = random.nextInt(size);
        if (multiverse.isPresent() && rand >= multiverse.get()) {
            rand++;
        }
        return DimensionHelper.getRegistryKey(rand);
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

    public static ResourceKey<Level> getRegistryKey(int index) {
        if (index == 0) {
            return Level.OVERWORLD;
        }
        return ResourceKey.create(Registries.DIMENSION, getResourceLocation(index));
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
