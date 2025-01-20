package io.github.davidqf555.minecraft.multiverse.common.world.blocks;

import io.github.davidqf555.minecraft.multiverse.common.util.TagUtil;
import io.github.davidqf555.minecraft.multiverse.common.world.RiftPlacementHelper;
import io.github.davidqf555.minecraft.multiverse.registration.TileEntityRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftTileEntity extends BlockEntity {

    private static final RiftPlacement DEFAULT = new RiftPlacement(new Vec3(0, 0, 0), 0, 0, new Vec3(0, 1, 0), 0);
    private ResourceKey<Level> target = Level.OVERWORLD;
    private Vec3[][] collision = new Vec3[2][0];
    private RiftPlacement parent = DEFAULT;
    private Vec3[][] visual;

    protected RiftTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public RiftTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.RIFT.get(), pos, state);
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

    public ResourceKey<Level> getTarget() {
        return target;
    }

    public void setTarget(ResourceKey<Level> target) {
        this.target = target;
    }

    public Vec3 getNormal() {
        return getParent().normal();
    }

    public Vec3[][] getCollision() {
        return collision;
    }

    public void setCollision(Vec3[] collision) {
        this.collision = convert(collision);
    }

    public RiftPlacement getParent() {
        return parent;
    }

    public void setParent(RiftPlacement parent) {
        this.parent = parent;
        visual = null;
    }

    public Vec3[][] getVisual() {
        if (visual == null) {
            RiftPlacement parent = getParent();
            visual = parent.calculateLayers(BlockPos.ZERO);
        }
        return visual;
    }

    public boolean isColliding(AABB bounds) {
        return RiftPlacementHelper.intersects(getCollision(), getNormal(), bounds);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("Target", getTarget().location().toString());
        Vec3 offset = Vec3.atLowerCornerOf(getBlockPos());
        ListTag collision = new ListTag();
        for (Vec3 point : getCollision()[0]) {
            collision.add(TagUtil.writeVec(point.subtract(offset)));
        }
        tag.put("Vertices", collision);
        CompoundTag parent = getParent().serialize();
        tag.put("Parent", parent);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("Target", CompoundTag.TAG_STRING)) {
            setTarget(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("Target"))));
        }
        if (tag.contains("Vertices", Tag.TAG_LIST)) {
            Vec3 offset = Vec3.atLowerCornerOf(getBlockPos());
            ListTag list = tag.getList("Vertices", Tag.TAG_COMPOUND);
            List<Vec3> vertices = new ArrayList<>();
            for (Tag val : list) {
                Vec3 point = TagUtil.readVec((CompoundTag) val);
                if (point != null) {
                    vertices.add(point.add(offset));
                }
            }
            setCollision(vertices.toArray(Vec3[]::new));
        }
        if (tag.contains("Parent", Tag.TAG_COMPOUND)) {
            RiftPlacement placement = RiftPlacement.deserialize(tag.getCompound("Parent"));
            if (placement != null) {
                setParent(placement);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

}
