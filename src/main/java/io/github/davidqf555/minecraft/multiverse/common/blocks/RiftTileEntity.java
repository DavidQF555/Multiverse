package io.github.davidqf555.minecraft.multiverse.common.blocks;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.registration.POIRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.TileEntityRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftTileEntity extends BlockEntity implements ITeleporter {

    private int target;

    protected RiftTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public RiftTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.RIFT.get(), pos, state);
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Pair<Integer, Integer> getColors(Random rand) {
        int color = FastColor.ARGB32.color(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        int particles = FastColor.ARGB32.color(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        return Pair.of(color, particles);
    }

    public Random getColorsRandom() {
        return new Random(getSeed());
    }

    public Pair<Integer, Integer> getColors() {
        return getColors(getColorsRandom());
    }

    public long getSeed() {
        Level world = getLevel();
        if (world != null) {
            return DimensionHelper.getSeed(world.getBiomeManager().biomeZoomSeed, getTarget(), true);
        }
        return 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Target", getTarget());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Target", CompoundTag.TAG_INT)) {
            setTarget(tag.getInt("Target"));
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return serializeNBT();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        deserializeNBT(pkt.getTag());
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        DimensionType target = destWorld.dimensionType();
        double scale = DimensionType.getTeleportationScale(entity.level.dimensionType(), target);
        BlockPos rift = getBlockPos();
        BlockPos scaled = new BlockPos(rift.getX() * scale, rift.getY(), rift.getZ() * scale);
        WorldBorder border = destWorld.getWorldBorder();
        BlockPos clamped = new BlockPos(Mth.clamp(scaled.getX(), border.getMinX(), border.getMaxX()), Mth.clamp(scaled.getY(), 1, target.logicalHeight()), Mth.clamp(scaled.getZ(), border.getMinZ(), border.getMaxZ()));
        int current = DimensionHelper.getIndex(entity.level.dimension());
        return new PortalInfo(Vec3.atBottomCenterOf(getOrCreateRift(destWorld, destWorld.getRandom(), clamped, ServerConfigs.INSTANCE.riftRange.get(), current, level.getBlockState(rift))), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());

    }

    private BlockPos getOrCreateRift(ServerLevel dest, Random rand, BlockPos center, int range, int current, BlockState state) {
        PoiManager manager = dest.getPoiManager();
        PoiType poi = POIRegistry.RIFT.get();
        manager.ensureLoadedAndValid(dest, center, range);
        return manager.getInSquare(poi::equals, center, range, PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos)
                .filter(block -> {
                    BlockEntity tile = dest.getBlockEntity(block);
                    return tile instanceof RiftTileEntity && ((RiftTileEntity) tile).getTarget() == current;
                })
                .min(Comparator.comparingDouble(center::distSqr))
                .orElseGet(() -> {
                    FeatureRegistry.RIFT.get().place(new FeaturePlaceContext<>(Optional.empty(), dest, dest.getChunkSource().getGenerator(), rand, center, RiftConfig.of(Optional.of(current), state, false)));
                    return center;
                });
    }

}
