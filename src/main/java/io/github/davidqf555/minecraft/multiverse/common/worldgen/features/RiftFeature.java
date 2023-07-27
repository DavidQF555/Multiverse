package io.github.davidqf555.minecraft.multiverse.common.worldgen.features;

import com.mojang.math.Constants;
import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class RiftFeature extends Feature<RiftConfig> {

    public RiftFeature(Codec<RiftConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RiftConfig> context) {
        WorldGenLevel reader = context.level();
        ServerLevel world = reader.getLevel();
        RiftConfig config = context.config();
        Random rand = context.random();
        int target = config.getTarget().orElseGet(() -> {
            int current = DimensionHelper.getIndex(world.dimension());
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
        int totalWidth = size.getWidth(rand);
        int totalHeight = size.getHeight(rand);
        RiftConfig.Rotation rotation = config.getRotation();
        float xRot = rotation.getRotX(rand) * Constants.DEG_TO_RAD;
        float yRot = rotation.getRotY(rand) * Constants.DEG_TO_RAD;
        float zRot = rotation.getRotZ(rand) * Constants.DEG_TO_RAD;
        Vec3 centerVec = Vec3.atCenterOf(center);
        for (int y = -totalHeight; y <= totalHeight; y++) {
            int width = totalWidth * (totalHeight - Mth.abs(y)) / totalHeight;
            for (int x = -width; x <= width; x++) {
                Vec3 vec = new Vec3(x, y, 0).xRot(xRot).yRot(yRot).zRot(zRot);
                BlockPos pos = new BlockPos(centerVec.add(vec));
                if (canReplace(reader, pos)) {
                    if (!natural) {
                        reader.destroyBlock(pos, true);
                    }
                    setBlock(reader, pos, rift);
                    BlockEntity tile = reader.getBlockEntity(pos);
                    if (tile instanceof RiftTileEntity) {
                        ((RiftTileEntity) tile).setTarget(target);
                    }
                }
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        for (int k = -1; k <= 1; k++) {
                            BlockPos replace = pos.offset(i, j, k);
                            if (canReplace(reader, replace)) {
                                if (natural) {
                                    setBlock(reader, replace, air);
                                } else {
                                    reader.destroyBlock(replace, true);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean canReplace(WorldGenLevel reader, BlockPos pos) {
        int blockY = pos.getY();
        return blockY >= 0 && blockY < reader.getMaxBuildHeight() && reader.getBlockState(pos).getDestroySpeed(reader, pos) != -1;
    }

}
