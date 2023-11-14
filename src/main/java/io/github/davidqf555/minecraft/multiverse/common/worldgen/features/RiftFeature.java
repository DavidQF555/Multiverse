package io.github.davidqf555.minecraft.multiverse.common.worldgen.features;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

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
        int target = config.getTarget().orElseGet(() -> {
            int current = DimensionHelper.getIndex(reader.getLevel().dimension());
            int dim = rand.nextInt(ServerConfigs.INSTANCE.maxDimensions.get());
            return dim < current ? dim : dim + 1;
        });
        BlockState rift = config.getBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        boolean natural = config.isNatural();
        BlockPos center = context.origin();
        if (!natural) {
            reader.getLevel().levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, center, 0);
        }
        RiftConfig.Size size = config.getSize();
        double width = size.getWidth(rand);
        double height = size.getHeight(rand);
        double depth = 0.5;
        RiftConfig.Rotation rotation = config.getRotation(rand);
        int maxLength = Mth.ceil(Math.max(Math.max(width, height), depth));
        for (int dY = -maxLength; dY <= maxLength; dY++) {
            for (int dX = -maxLength; dX <= maxLength; dX++) {
                for (int dZ = -maxLength; dZ <= maxLength; dZ++) {
                    BlockPos pos = center.offset(dX, dY, dZ);
                    if (canReplace(reader, pos) && isRift(center, pos, rotation, width, height, depth)) {
                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                for (int k = -1; k <= 1; k++) {
                                    BlockPos destroy = pos.offset(i, j, k);
                                    if (canDestroy(reader, destroy)) {
                                        if (natural) {
                                            setBlock(reader, destroy, air);
                                        } else {
                                            reader.destroyBlock(destroy, true);
                                        }
                                    }
                                }
                            }
                        }
                        setBlock(reader, pos, rift);
                        BlockEntity tile = reader.getBlockEntity(pos);
                        if (tile instanceof RiftTileEntity) {
                            ((RiftTileEntity) tile).setTarget(target);
                        }
                    }
                }
            }
        }
        return true;
    }

    protected boolean canReplace(WorldGenLevel reader, BlockPos pos) {
        return !reader.isOutsideBuildHeight(pos) && reader.getBlockState(pos).getDestroySpeed(reader, pos) != -1;
    }

    protected boolean canDestroy(WorldGenLevel reader, BlockPos pos) {
        return canReplace(reader, pos) && reader.getBlockState(pos).getFluidState().isEmpty();
    }

    protected boolean isRift(BlockPos center, BlockPos pos, RiftConfig.Rotation rotation, double width, double height, double depth) {
        return isColliding(Vec3.atCenterOf(center), rotation, width, height, depth, Vec3.atCenterOf(pos));
    }

    protected boolean isColliding(Vec3 center, RiftConfig.Rotation rotation, double width, double height, double depth, Vec3 pos) {
        Vec3 normal = rotation.getAxis();
        Vec3 line = pos.subtract(center);
        Vec3 proj = normal.scale(normal.dot(line));
        if (proj.lengthSqr() > depth * depth) {
            return false;
        }
        Vec3 cross = normal.cross(new Vec3(0, 1, 0));
        if (cross.lengthSqr() == 0) {
            cross = new Vec3(1, 0, 0);
        }
        Vec3 horizontal = rotate(cross, normal, rotation.getAngle());
        Vec3 comp = line.subtract(proj);
        Vec3 projWidth = horizontal.scale(horizontal.dot(comp) / horizontal.lengthSqr());
        double compWidth = projWidth.length();
        if (compWidth > width) {
            return false;
        }
        double heightBound = height - Math.abs(compWidth) * height / width;
        return comp.subtract(projWidth).lengthSqr() <= heightBound * heightBound;
    }

    private Vec3 rotate(Vec3 original, Vec3 axis, float angle) {
        Vec3 parallel = axis.scale(axis.dot(original) / axis.lengthSqr());
        Vec3 perp = original.subtract(parallel);
        Vec3 cross = axis.cross(perp).normalize();
        Vec3 rotate = perp.scale(Mth.cos(angle * Mth.DEG_TO_RAD)).add(cross.scale(Mth.sin(angle * Mth.DEG_TO_RAD) * perp.length()));
        return rotate.add(parallel);
    }

}
