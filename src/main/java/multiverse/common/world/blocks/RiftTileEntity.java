package multiverse.common.world.blocks;

import multiverse.client.ClientConfigs;
import multiverse.common.util.TagUtil;
import multiverse.common.world.RiftPlacementHelper;
import multiverse.registration.TileEntityRegistry;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftTileEntity extends BlockEntity {

    private static final RiftPlacement DEFAULT = new RiftPlacement(new Vec3(0, 0, 0), 0, 0, new Vec3(0, 1, 0), 0);
    private ResourceKey<Level> target = Level.OVERWORLD;
    private Vec3[][] collision = new Vec3[2][0];
    private RiftPlacement parent = DEFAULT;
    private Vec3[][] visual;
    private double[] visualAreas;

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
        visualAreas = null;
    }

    public Vec3[][] getVisual() {
        if (visual == null) {
            RiftPlacement parent = getParent();
            visual = parent.calculateLayers(BlockPos.ZERO);
        }
        return visual;
    }

    protected double[] getVisualAreas() {
        if (visualAreas == null) {
            Vec3[] points = getVisual()[ClientConfigs.INSTANCE.riftLayers.get() - 1];
            visualAreas = new double[points.length];
            if (points.length >= 3) {
                Vec3 center = Arrays.stream(points).reduce(Vec3.ZERO, Vec3::add).scale(1.0 / points.length);
                for (int i = 0; i < points.length; i++) {
                    int j = i == points.length - 1 ? 0 : i + 1;
                    Vec3 d1 = points[i].subtract(center);
                    Vec3 d2 = points[j].subtract(center);
                    visualAreas[i] = d1.cross(d2).length() / 2;
                }
            }
        }
        return visualAreas;
    }

    public double getTotalVisualArea() {
        return Arrays.stream(getVisualAreas()).sum();
    }

    public Optional<Vec3> getRandomVisualPoint(RandomSource rand) {
        Vec3[] full = getVisual()[ClientConfigs.INSTANCE.riftLayers.get() - 1];
        if (full.length == 0) {
            return Optional.empty();
        }
        if (full.length == 1) {
            return Optional.of(full[0]);
        }
        if (full.length == 2) {
            double factor = rand.nextDouble();
            return Optional.of(full[0].scale(factor).add(full[1].scale(1 - factor)));
        }
        double[] areas = getVisualAreas();
        double total = Arrays.stream(areas).sum();
        double selected = rand.nextDouble() * total;
        int i = 0;
        for (int j = 0; j < full.length; j++) {
            total -= areas[j];
            if (total <= selected) {
                i = j;
                break;
            }
        }
        double r1 = rand.nextDouble();
        double r2 = rand.nextDouble();
        Vec3 center = Arrays.stream(full).reduce(Vec3.ZERO, Vec3::add).scale(1.0 / full.length).scale(1 - Math.sqrt(r1));
        Vec3 neighbor = full[i == full.length - 1 ? 0 : i + 1].scale(Math.sqrt(1 - r1) * (1 - r2));
        return Optional.of(full[i].scale(r2 * Math.sqrt(r1)).add(center).add(neighbor));
    }

    public boolean isColliding(AABB bounds) {
        return RiftPlacementHelper.intersects(getCollision(), getParent().normal(), bounds);
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
