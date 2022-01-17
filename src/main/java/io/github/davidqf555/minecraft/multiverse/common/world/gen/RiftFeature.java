package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import io.github.davidqf555.minecraft.multiverse.common.RegistryHandler;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
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
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class RiftFeature extends Feature<NoFeatureConfig> {

    public static final ConfiguredFeature<NoFeatureConfig, RiftFeature> CONFIG = new ConfiguredFeature<>(new RiftFeature(), NoFeatureConfig.INSTANCE);

    public RiftFeature() {
        super(NoFeatureConfig.CODEC);
    }

    @Override
    public boolean place(ISeedReader reader, ChunkGenerator gen, Random rand, BlockPos pos, NoFeatureConfig config) {
        if (rand.nextDouble() < 0.05) {
            int index = rand.nextInt(ServerConfigs.INSTANCE.maxDimensions.get() + 1);
            createRift(reader, rand, pos.offset(0, rand.nextInt(reader.getHeight()), 0), index);
            return true;
        }
        return false;
    }

    public void createRift(ISeedReader reader, Random rand, BlockPos center, int target) {
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
                        ((RiftTileEntity) tile).setWorld(target);
                    }
                }
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        for (int k = -1; k <= 1; k++) {
                            BlockPos replace = pos.offset(i, j, k);
                            if (canReplace(reader, replace) && !reader.getBlockState(replace).equals(rift)) {
                                setBlock(reader, replace, air);
                            }
                        }
                    }
                }
            }
        }
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
