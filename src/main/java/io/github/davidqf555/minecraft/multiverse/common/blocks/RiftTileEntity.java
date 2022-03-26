package io.github.davidqf555.minecraft.multiverse.common.blocks;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.registration.FeatureRegistry;
import io.github.davidqf555.minecraft.multiverse.common.registration.POIRegistry;
import io.github.davidqf555.minecraft.multiverse.common.registration.TileEntityRegistry;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.rifts.RiftConfig;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftTileEntity extends TileEntity implements ITeleporter {

    private int target;

    protected RiftTileEntity(TileEntityType<?> type) {
        super(type);
    }

    public RiftTileEntity() {
        this(TileEntityRegistry.RIFT.get());
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Pair<Integer, Integer> getColors(Random rand) {
        int color = ColorHelper.PackedColor.color(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        int particles = ColorHelper.PackedColor.color(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        return Pair.of(color, particles);
    }

    public Random getColorsRandom() {
        return new Random(getSeed());
    }

    public Pair<Integer, Integer> getColors() {
        return getColors(getColorsRandom());
    }

    public long getSeed() {
        World world = getLevel();
        if (world != null) {
            return DimensionHelper.getSeed(world.getBiomeManager().biomeZoomSeed, getTarget(), true);
        }
        return 0;
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
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        deserializeNBT(pkt.getTag());
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
        return new PortalInfo(Vector3d.atBottomCenterOf(getOrCreateRift(destWorld, destWorld.getRandom(), clamped, ServerConfigs.INSTANCE.riftRange.get(), current, level.getBlockState(rift))), entity.getDeltaMovement(), entity.yRot, entity.xRot);
    }

    private BlockPos getOrCreateRift(ServerWorld dest, Random rand, BlockPos center, int range, int current, BlockState state) {
        PointOfInterestManager manager = dest.getPoiManager();
        PointOfInterestType poi = POIRegistry.RIFT.get();
        manager.ensureLoadedAndValid(dest, center, range);
        return manager.getInSquare(poi::equals, center, range, PointOfInterestManager.Status.ANY)
                .map(PointOfInterest::getPos)
                .filter(block -> {
                    TileEntity tile = dest.getBlockEntity(block);
                    return tile instanceof RiftTileEntity && ((RiftTileEntity) tile).getTarget() == current;
                })
                .min(Comparator.comparingDouble(center::distSqr))
                .orElseGet(() -> {
                    FeatureRegistry.RIFT.get().place(dest, dest.getChunkSource().getGenerator(), rand, center, RiftConfig.of(Optional.of(current), state, false));
                    return center;
                });
    }

}
