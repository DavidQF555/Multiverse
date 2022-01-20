package io.github.davidqf555.minecraft.multiverse.common.blocks;

import io.github.davidqf555.minecraft.multiverse.common.RegistryHandler;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.RiftConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.DimensionType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class RiftTileEntity extends TileEntity implements ITeleporter {

    private static final int RANGE = 64;
    private int target;

    public RiftTileEntity() {
        super(RegistryHandler.RIFT_TILE_ENTITY_TYPE.get());
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putInt("Target", getTarget());
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if (nbt.contains("Target", Constants.NBT.TAG_INT)) {
            setTarget(nbt.getInt("Target"));
        }
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        DimensionType target = destWorld.dimensionType();
        double scale = DimensionType.getTeleportationScale(entity.level.dimensionType(), target);
        BlockPos rift = getBlockPos();
        BlockPos scaled = new BlockPos(rift.getX() * scale, rift.getY(), rift.getZ() * scale);
        WorldBorder border = destWorld.getWorldBorder();
        BlockPos clamped = new BlockPos(MathHelper.clamp(scaled.getX(), border.getMinX(), border.getMaxX()), MathHelper.clamp(scaled.getY(), 1, target.logicalHeight()), MathHelper.clamp(scaled.getZ(), border.getMinZ(), border.getMaxZ()));
        int current = DimensionHelper.getIndex(entity.level.dimension());
        boolean temporary = entity.level.getBlockState(rift).getValue(RiftBlock.TEMPORARY);
        return new PortalInfo(Vector3d.atBottomCenterOf(getOrCreateRift(destWorld, destWorld.getRandom(), clamped, RANGE, current, temporary)), entity.getDeltaMovement(), entity.yRot, entity.xRot);
    }

    private BlockPos getOrCreateRift(ServerWorld dest, Random rand, BlockPos center, int range, int current, boolean temporary) {
        PointOfInterestManager manager = dest.getPoiManager();
        manager.ensureLoadedAndValid(dest, center, range);
        return manager.getInSquare(type -> type == RegistryHandler.RIFT_POI_TYPE.get(), center, range, PointOfInterestManager.Status.ANY).map(PointOfInterest::getPos).filter(block -> {
            TileEntity tile = dest.getBlockEntity(block);
            return tile instanceof RiftTileEntity && ((RiftTileEntity) tile).getTarget() == current;
        }).min(Comparator.comparingDouble(center::distSqr)).orElseGet(() -> {
            RegistryHandler.RIFT_FEATURE.get().place(dest, dest.getChunkSource().getGenerator(), rand, center, RiftConfig.of(Optional.of(current), temporary));
            return center;
        });
    }

}
