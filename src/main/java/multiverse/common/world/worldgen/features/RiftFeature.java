package multiverse.common.world.worldgen.features;

import com.mojang.serialization.Codec;
import multiverse.common.world.RiftHelper;
import multiverse.common.world.RiftPlacementHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Random;

@ParametersAreNonnullByDefault
public class RiftFeature extends Feature<RiftConfig> {

    public RiftFeature(Codec<RiftConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RiftConfig> context) {
        WorldGenLevel reader = context.level();
        RiftConfig config = context.config();
        Random rand = context.random();
        BlockPos origin = context.origin();
        BlockState state = config.getBlockState();
        Optional<ResourceKey<Level>> target = RiftHelper.randomTargetDimension(rand, reader.getLevel().dimension());
        if (target.isEmpty()) {
            return false;
        }
        double width = config.getWidth(rand);
        double height = config.getHeight(rand);
        Vec3 normal = new Vec3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
        float angle = rand.nextFloat(180);
        Vec3 size = getAxisAlignedSize(width, height, normal, angle);
        Vec3 center = new Vec3(30, reader.getHeight(), 30).subtract(size);
        if (center.x() < 0 || center.y() < 0 || center.z() < 0) {
            return false;
        }
        double x = (center.x() == 0 ? 0 : rand.nextDouble(center.x())) + origin.getX() + size.x() / 2 + 1;
        double z = (center.z() == 0 ? 0 : rand.nextDouble(center.z())) + origin.getZ() + size.z() / 2 + 1;
        double y = (center.y() == 0 ? 0 : rand.nextDouble(center.y())) + reader.getMinBuildHeight();
        RiftPlacementHelper.place(reader, state, target.get(), new Vec3(x, y, z), normal, angle, width, height, RiftPlacementHelper.ReplacementType.FEATURE_REMOVE);
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
