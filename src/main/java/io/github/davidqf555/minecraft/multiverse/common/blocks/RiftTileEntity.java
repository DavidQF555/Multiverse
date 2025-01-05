package io.github.davidqf555.minecraft.multiverse.common.blocks;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.util.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.util.RiftCoordinationHelper;
import io.github.davidqf555.minecraft.multiverse.common.util.RiftPlacementHelper;
import io.github.davidqf555.minecraft.multiverse.registration.TileEntityRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftTileEntity extends BlockEntity implements ITeleporter {

    private Vec3 normal = new Vec3(0, 1, 0);
    private Vec3[][] vertices = new Vec3[2][0];
    private ResourceKey<Level> target = Level.OVERWORLD;
    private AABB bounds;

    protected RiftTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public RiftTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.RIFT.get(), pos, state);
    }

    public ResourceKey<Level> getTarget() {
        return target;
    }

    public void setTarget(ResourceKey<Level> target) {
        this.target = target;
    }

    protected static Vec3[][] convert(Vec3[] vertices) {
        if (vertices.length < 2) {
            return new Vec3[2][0];
        }
        Vec3[][] all = new Vec3[2][vertices.length];
        System.arraycopy(vertices, 0, all[0], 0, vertices.length);
        all[1][all.length - 1] = all[0][0];
        System.arraycopy(all[0], 1, all[1], 0, all.length - 1);
        return all;
    }

    public Vec3 getNormal() {
        return normal;
    }

    public void setNormal(Vec3 normal) {
        this.normal = normal.lengthSqr() == 0 ? new Vec3(0, 1, 0) : normal;
    }

    public Vec3[][] getVertices() {
        return vertices;
    }

    public void setVertices(Vec3[] vertices) {
        this.vertices = convert(vertices);
        bounds = null;
    }

    @Override
    public AABB getRenderBoundingBox() {
        if (bounds == null) {
            Vec3[][] vertices = getVertices();
            double minX = Arrays.stream(vertices[0]).mapToDouble(Vec3::x).min().orElse(0);
            double maxX = Arrays.stream(vertices[0]).mapToDouble(Vec3::x).max().orElse(0);
            double minY = Arrays.stream(vertices[0]).mapToDouble(Vec3::y).min().orElse(0);
            double maxY = Arrays.stream(vertices[0]).mapToDouble(Vec3::y).max().orElse(0);
            double minZ = Arrays.stream(vertices[0]).mapToDouble(Vec3::z).min().orElse(0);
            double maxZ = Arrays.stream(vertices[0]).mapToDouble(Vec3::z).max().orElse(0);
            bounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return bounds;
    }

    public boolean isColliding(AABB bounds) {
        return RiftPlacementHelper.intersects(getVertices(), getNormal(), bounds);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Target", getTarget().location().toString());
        ListTag vertices = new ListTag();
        for (Vec3 point : getVertices()[0]) {
            CompoundTag com = new CompoundTag();
            com.putDouble("X", point.x());
            com.putDouble("Y", point.y());
            com.putDouble("Z", point.z());
            vertices.add(com);
        }
        tag.put("Vertices", vertices);
        CompoundTag normal = new CompoundTag();
        Vec3 val = getNormal();
        normal.putDouble("X", val.x());
        normal.putDouble("Y", val.y());
        normal.putDouble("Z", val.z());
        tag.put("Normal", normal);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Target", CompoundTag.TAG_STRING)) {
            setTarget(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString("Target"))));
        }
        if (tag.contains("Vertices", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Vertices", Tag.TAG_COMPOUND);
            List<Vec3> vertices = new ArrayList<>();
            for (Tag val : list) {
                if (((CompoundTag) val).contains("X", Tag.TAG_DOUBLE) && ((CompoundTag) val).contains("Y", Tag.TAG_DOUBLE) && ((CompoundTag) val).contains("Z", Tag.TAG_DOUBLE)) {
                    vertices.add(new Vec3(((CompoundTag) val).getDouble("X"), ((CompoundTag) val).getDouble("Y"), ((CompoundTag) val).getDouble("Z")));
                }
            }
            setVertices(vertices.toArray(Vec3[]::new));
        }
        if (tag.contains("Normal", Tag.TAG_COMPOUND)) {
            CompoundTag normal = tag.getCompound("Normal");
            if (normal.contains("X", Tag.TAG_DOUBLE) && normal.contains("Y", Tag.TAG_DOUBLE) && normal.contains("Z", Tag.TAG_DOUBLE)) {
                setNormal(new Vec3(normal.getDouble("X"), normal.getDouble("Y"), normal.getDouble("Z")));
            }
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
        DimensionType from = entity.level.dimensionType();
        BlockPos rift = getBlockPos();
        Vec3 scaled = DimensionHelper.translate(Vec3.atCenterOf(rift), from, target, true);
        WorldBorder border = destWorld.getWorldBorder();
        BlockPos clamped = border.clampToBounds(scaled.x(), scaled.y(), scaled.z());
        Vec3 pos = RiftCoordinationHelper.getOrCreateRift(destWorld, entity.level.dimension(), Vec3.atCenterOf(clamped), level.getBlockState(rift).getValue(RiftBlock.TEMPORARY), ServerConfigs.INSTANCE.riftRange.get(), RiftPlacementHelper.ReplacementType.DESTROY);
        return new PortalInfo(pos, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
    }

}
