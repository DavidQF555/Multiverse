package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import io.github.davidqf555.minecraft.multiverse.common.RegistryHandler;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class RiftFeature extends Feature<RiftConfig> {

    public static final RiftFeature INSTANCE = new RiftFeature();
    public static final ConfiguredFeature<?, ?> CONFIG = new ConfiguredFeature<>(INSTANCE, RiftConfig.UNKNOWN).decorated(RiftPlacement.CONFIG).chance(ServerConfigs.INSTANCE.riftChance.get());

    public RiftFeature() {
        super(RiftConfig.CODEC);
    }

    @Override
    public boolean place(ISeedReader reader, ChunkGenerator gen, Random rand, BlockPos center, RiftConfig config) {
        int target = config.getTarget().orElseGet(() -> {
            int current = DimensionHelper.getIndex(reader.getLevel().dimension());
            int world = rand.nextInt(ServerConfigs.INSTANCE.maxDimensions.get());
            return world < current ? world : world + 1;
        });
        BlockState rift = RegistryHandler.RIFT_BLOCK.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        int totalWidth = getWidth(rand);
        int totalHeight = getHeight(rand);
        float yaw = getYaw(rand);
        float pitch = getPitch(rand);
        float roll = getRoll(rand);
        Vector3d centerVec = Vector3d.atCenterOf(center);
        for (int y = -totalHeight; y <= totalHeight; y++) {
            int width = totalWidth * (totalHeight - MathHelper.abs(y)) / totalHeight;
            for (int x = -width; x <= width; x++) {
                Vector3d vec = new Vector3d(x + 0.5, y + 0.5, 0.5).xRot(pitch).yRot(yaw).zRot(roll);
                BlockPos pos = new BlockPos(centerVec.add(vec.x(), vec.y(), vec.z()));
                if (canReplace(reader, pos)) {
                    setBlock(reader, pos, rift);
                    TileEntity tile = reader.getBlockEntity(pos);
                    if (tile instanceof RiftTileEntity) {
                        ((RiftTileEntity) tile).setTarget(target);
                        tile.setChanged();
                    }
                }
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        for (int k = -1; k <= 1; k++) {
                            BlockPos replace = pos.offset(i, j, k);
                            BlockState state = reader.getBlockState(replace);
                            if (canReplace(reader, replace) && !state.equals(rift) && state.canOcclude()) {
                                setBlock(reader, replace, air);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean canReplace(ISeedReader reader, BlockPos pos) {
        int blockY = pos.getY();
        return blockY >= 0 && blockY < reader.getMaxBuildHeight() && reader.getBlockState(pos).getDestroySpeed(reader, pos) != -1;
    }

    protected int getWidth(Random rand) {
        return 1 + rand.nextInt(4);
    }

    protected int getHeight(Random rand) {
        return 6 + rand.nextInt(5);
    }

    protected float getYaw(Random rand) {
        return rand.nextFloat() * (float) Math.PI;
    }

    protected float getPitch(Random rand) {
        return rand.nextFloat() * (float) Math.PI;
    }

    protected float getRoll(Random rand) {
        return rand.nextFloat() * (float) Math.PI;
    }
}
