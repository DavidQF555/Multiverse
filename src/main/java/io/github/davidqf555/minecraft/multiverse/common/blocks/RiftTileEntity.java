package io.github.davidqf555.minecraft.multiverse.common.blocks;

import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.util.RiftHelper;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.POIRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.TileEntityRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftTileEntity extends BlockEntity implements Portal {

    public static final TeleportTransition.PostTeleportTransition SLOW_FALLING = entity -> {
        if (entity instanceof LivingEntity) {
            int duration = ServerConfigs.INSTANCE.slowFalling.get();
            if (duration > 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration, 1, false, true));
            }
        }
    };
    private Vec3 normal = new Vec3(0, 1, 0);
    private Vec3[][] vertices = new Vec3[2][0];
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
    }

    public boolean isColliding(AABB bounds) {
        return RiftHelper.intersects(getVertices(), getNormal(), bounds);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("Target", getTarget());
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
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("Target", CompoundTag.TAG_INT)) {
            setTarget(tag.getInt("Target"));
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
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithFullMetadata(provider);
    }

    @Nullable
    @Override
    public TeleportTransition getPortalDestination(ServerLevel fromWorld, Entity entity, BlockPos pos) {
        MinecraftServer server = fromWorld.getServer();
        int target = getTarget();
        if (DimensionHelper.getWorld(server, target).isPresent() || entity.getType().is(MultiverseTags.GENERATE_MULTIVERSE)) {
            ServerLevel toWorld = DimensionHelper.getOrCreateWorld(server, target);
            DimensionType to = toWorld.dimensionType();
            DimensionType from = fromWorld.dimensionType();
            BlockPos rift = getBlockPos();
            Vec3 scaled = DimensionHelper.translate(Vec3.atBottomCenterOf(rift), from, to, true);
            WorldBorder border = toWorld.getWorldBorder();
            BlockPos clamped = border.clampToBounds(scaled.x(), scaled.y(), scaled.z());
            int current = DimensionHelper.getIndex(entity.level().dimension());
            return new TeleportTransition(toWorld, getOrCreateRift(toWorld, toWorld.getRandom(), Vec3.atBottomCenterOf(clamped), ServerConfigs.INSTANCE.riftRange.get(), current, level.getBlockState(rift)), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), SLOW_FALLING);
        }
        return null;
    }

    private Vec3 getOrCreateRift(ServerLevel dest, RandomSource rand, Vec3 center, int range, int current, BlockState state) {
        PoiManager manager = dest.getPoiManager();
        ResourceLocation poi = POIRegistry.RIFT.getId();
        BlockPos pos = BlockPos.containing(center);
        manager.ensureLoadedAndValid(dest, pos, range);
        return manager.getInSquare(holder -> holder.is(poi), pos, range, PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos)
                .filter(block -> {
                    BlockEntity tile = dest.getBlockEntity(block);
                    return tile instanceof RiftTileEntity && ((RiftTileEntity) tile).getTarget() == current;
                })
                .min(Comparator.comparingDouble(pos::distSqr))
                .map(Vec3::atBottomCenterOf)
                .orElseGet(() -> {
                    RiftHelper.place(dest, rand, state, Optional.of(current), Optional.empty(), center, true);
                    return center;
                });
    }

}
