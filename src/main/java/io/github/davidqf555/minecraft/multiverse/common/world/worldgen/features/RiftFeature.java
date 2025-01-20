package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.features;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.RiftPlacementHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class RiftFeature extends Feature<RiftConfig> {

    public RiftFeature(Codec<RiftConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RiftConfig> context) {
        WorldGenLevel reader = context.level();
        RiftConfig config = context.config();
        RandomSource rand = context.random();
        BlockPos origin = context.origin();
        BlockState state = config.getBlockState();
        double width = config.getWidth(rand);
        double height = config.getHeight(rand);
        Vec3 normal = new Vec3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
        float angle = rand.nextFloat() * 180;
        Vec3 size = getAxisAlignedSize(width, height, normal, angle);
        Vec3 center = new Vec3(30, reader.getHeight(), 30).subtract(size);
        if (center.x() < 0 || center.y() < 0 || center.z() < 0) {
            return false;
        }
        double x = rand.nextDouble() * center.x() + origin.getX() + size.x() / 2 + 1;
        double z = rand.nextDouble() * center.z() + origin.getZ() + size.z() / 2 + 1;
        double y = rand.nextDouble() * center.y() + reader.getMinY();
        ResourceKey<Level> target = DimensionHelper.randomMultiverseDimension(rand, Optional.of(reader.getLevel().dimension()));
        RiftPlacementHelper.place(reader, reader, state, target, new Vec3(x, y, z), normal, angle, width, height, RiftPlacementHelper.ReplacementType.FEATURE_REMOVE);
        return true;
    }

    private Vec3 getAxisAlignedSize(double width, double height, Vec3 normal, float angle) {
        Vec3[] vertices = RiftPlacementHelper.calculateVertices(Vec3.ZERO, normal, angle, width, height)[0];
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for (Vec3 point : vertices) {
            if (point.x() < minX) {
                minX = point.x();
            }
            if (point.x() > maxX) {
                maxX = point.x();
            }
            if (point.y() < minY) {
                minY = point.y();
            }
            if (point.y() > maxY) {
                maxY = point.y();
            }
            if (point.z() < minZ) {
                minZ = point.z();
            }
            if (point.z() > maxZ) {
                maxZ = point.z();
            }
        }
        return new Vec3(maxX - minX, maxY - minY, maxZ - minZ);
    }

}
