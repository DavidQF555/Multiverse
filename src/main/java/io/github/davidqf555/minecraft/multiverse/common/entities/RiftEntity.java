package io.github.davidqf555.minecraft.multiverse.common.entities;

import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.entities.serializers.DoubleSerializer;
import io.github.davidqf555.minecraft.multiverse.common.entities.serializers.VectorSerializer;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.registration.POIRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public class RiftEntity extends Entity implements ITeleporter {

    private static final EntityDataAccessor<Vec3> AXIS = SynchedEntityData.defineId(RiftEntity.class, VectorSerializer.INSTANCE);
    private static final EntityDataAccessor<Float> ANGLE = SynchedEntityData.defineId(RiftEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Double> WIDTH = SynchedEntityData.defineId(RiftEntity.class, DoubleSerializer.INSTANCE);
    private static final EntityDataAccessor<Double> HEIGHT = SynchedEntityData.defineId(RiftEntity.class, DoubleSerializer.INSTANCE);
    private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(RiftEntity.class, EntityDataSerializers.INT);
    private ConvexQuadrilateral3D shape;

    public RiftEntity(EntityType<? extends RiftEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setNoGravity(true);
    }

    public static void spawnRandom() {

    }

    @Override
    public void tick() {
        super.tick();
        for(Vec3[] opposites : getShape().getVertices()){
            for(Vec3 pos : opposites) {
                level.addParticle(ParticleTypes.FLAME, pos.x(), pos.y(), pos.z(), 0, 0, 0);
            }
        }
        if(!level.isClientSide()) {
            MinecraftServer server = level.getServer();
            ConvexQuadrilateral3D shape = getShape();
            int target = getTarget();
            for (Entity entity : level.getEntities(this, getShape().getBounds())) {
                if (entity.canChangeDimensions() && !entity.isPassenger() && !entity.isVehicle() && !(entity instanceof ItemEntity) && shape.isColliding(entity.getBoundingBox())) {
                    if (!entity.isOnPortalCooldown()) {
                        if (DimensionHelper.getWorld(server, target).isPresent() || entity.getType().is(MultiverseTags.GENERATE_MULTIVERSE)) {
                            ServerLevel dim = DimensionHelper.getOrCreateWorld(server, target);
                            entity.changeDimension(dim, this);
                        }
                    }
                    entity.setPortalCooldown();
                }
            }
        }
    }

    @Override
    public boolean isColliding(BlockPos pPos, BlockState pState) {
        ConvexQuadrilateral3D shape = getShape();
        for(AABB bounds : pState.getCollisionShape(level, pPos).toAabbs()) {
            if(shape.isColliding(bounds.move(pPos))) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected AABB makeBoundingBox() {
        return getShape().getBounds();
    }

    @Override
    public void setPos(double x, double y, double z) {
        shape = null;
        super.setPos(x, y, z);
    }

    public ConvexQuadrilateral3D getShape(){
        if(shape == null) {
            shape = new ConvexQuadrilateral3D(position(), getAxis(), getAngle(), getWidth(), getHeight());
        }
        return shape;
    }

    public int getTarget(){
        return getEntityData().get(TARGET);
    }

    public void setTarget(int target) {
        getEntityData().set(TARGET, target);
    }

    public Vec3 getAxis(){
        return getEntityData().get(AXIS);
    }

    public void setAxis(Vec3 axis) {
        getEntityData().set(AXIS, axis);
        shape = null;
    }

    public float getAngle(){
        return getEntityData().get(ANGLE);
    }

    public void setAngle(float angle) {
        getEntityData().set(ANGLE, angle);
        shape = null;
    }

    public double getWidth(){
        return getEntityData().get(WIDTH);
    }

    public void setWidth(double width) {
        getEntityData().set(WIDTH, width);
        shape = null;
    }

    public double getHeight(){
        return getEntityData().get(HEIGHT);
    }

    public void setHeight(double height) {
        getEntityData().set(HEIGHT, height);
        shape = null;
    }

    @Override
    protected void defineSynchedData() {
        SynchedEntityData data = getEntityData();
        data.define(AXIS, new Vec3(0, 1, 0));
        data.define(ANGLE, 0f);
        data.define(WIDTH, 1.0);
        data.define(HEIGHT, 1.0);
        data.define(TARGET, 1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if(tag.contains("AxisX", Tag.TAG_DOUBLE) && tag.contains("AxisY", Tag.TAG_DOUBLE) && tag.contains("AxisZ", Tag.TAG_DOUBLE)) {
            setAxis(new Vec3(tag.getDouble("AxisX"), tag.getDouble("AxisY"), tag.getDouble("AxisZ")));
        }
        if(tag.contains("Angle", Tag.TAG_FLOAT)) {
            setAngle(tag.getFloat("Angle"));
        }
        if(tag.contains("Width", Tag.TAG_DOUBLE)) {
            setWidth(tag.getDouble("Width"));
        }
        if(tag.contains("Height", Tag.TAG_DOUBLE)) {
            setWidth(tag.getDouble("Height"));
        }
        if(tag.contains("Target", Tag.TAG_INT)) {
            setTarget(tag.getInt("Target"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        Vec3 axis = getAxis();
        tag.putDouble("AxisX", axis.x());
        tag.putDouble("AxisY", axis.y());
        tag.putDouble("AxisZ", axis.z());
        tag.putFloat("Angle", getAngle());
        tag.putDouble("Width", getWidth());
        tag.putDouble("Height", getHeight());
        tag.putInt("Target", getTarget());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        DimensionType target = destWorld.dimensionType();
        DimensionType from = entity.level.dimensionType();
        BlockPos rift = blockPosition();
        Vec3 scaled = DimensionHelper.translate(Vec3.atBottomCenterOf(rift), from, target, true);
        WorldBorder border = destWorld.getWorldBorder();
        BlockPos clamped = border.clampToBounds(scaled.x(), scaled.y(), scaled.z());
        int current = DimensionHelper.getIndex(entity.level.dimension());
        return new PortalInfo(Vec3.atBottomCenterOf(getOrCreateRift(destWorld, destWorld.getRandom(), clamped, ServerConfigs.INSTANCE.riftRange.get(), current, level.getBlockState(rift))), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());

    }

    protected BlockPos getOrCreateRift(ServerLevel dest, Random rand, BlockPos center, int range, int current, BlockState state) {
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

    public static class ConvexQuadrilateral3D {

        private final Vec3[][] vertices = new Vec3[2][2];
        private final Vec3 normal;
        private AABB bounds;

        protected ConvexQuadrilateral3D(Vec3 center, Vec3 normal, float angle, double width, double height) {
            this.normal = normal;
            Vec3 vertical = normal.cross(new Vec3(0, 1, 0)).normalize();
            if (vertical.lengthSqr() == 0) {
                vertical = new Vec3(1, 0, 0);
            }
            Vec3 horizontal = normal.cross(vertical).normalize();
            vertices[0][0] = horizontal.scale(width / 2);
            vertices[0][1] = horizontal.scale(-width / 2);
            vertices[1][0] = vertical.scale(height / 2);
            vertices[1][1] = vertical.scale(-height / 2);
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    vertices[i][j] = center.add(rotate(vertices[i][j], normal, angle));
                }
            }
        }

        private static Vec3 rotate(Vec3 original, Vec3 axis, float angle) {
            Vec3 parallel = axis.scale(axis.dot(original) / axis.lengthSqr());
            Vec3 perp = original.subtract(parallel);
            Vec3 cross = axis.cross(perp).normalize();
            Vec3 rotate = perp.scale(Mth.cos(angle * Mth.DEG_TO_RAD)).add(cross.scale(Mth.sin(angle * Mth.DEG_TO_RAD) * perp.length()));
            return rotate.add(parallel);
        }

        public void doEffect(Consumer<BlockPos> effect) {

        }

        public Vec3[][] getVertices(){
            return vertices;
        }

        public boolean isColliding(AABB bounds) {
            // edge in bounds
            for(Vec3 v1 : vertices[0]) {
                for(Vec3 v2 : vertices[1]) {
                    if(bounds.intersects(v1, v2)) {
                        return true;
                    }
                }
            }
            // check if AABB fully enclosed by rift
            //can do by checking if a line of AABB intersects rift
            for(Vec3[] line : new Vec3[][]{
                    {new Vec3(bounds.minX, bounds.minY, bounds.minZ), new Vec3(bounds.maxX, bounds.minY, bounds.minZ)},
                    {new Vec3(bounds.minX, bounds.minY, bounds.maxZ), new Vec3(bounds.maxX, bounds.minY, bounds.maxZ)},
                    {new Vec3(bounds.minX, bounds.maxY, bounds.minZ), new Vec3(bounds.maxX, bounds.maxY, bounds.minZ)},
                    {new Vec3(bounds.minX, bounds.maxY, bounds.maxZ), new Vec3(bounds.maxX, bounds.maxY, bounds.maxZ)},
                    {new Vec3(bounds.minX, bounds.minY, bounds.minZ), new Vec3(bounds.minX, bounds.maxY, bounds.minZ)},
                    {new Vec3(bounds.minX, bounds.minY, bounds.maxZ), new Vec3(bounds.minX, bounds.maxY, bounds.maxZ)},
                    {new Vec3(bounds.maxX, bounds.minY, bounds.minZ), new Vec3(bounds.maxX, bounds.maxY, bounds.minZ)},
                    {new Vec3(bounds.maxX, bounds.minY, bounds.maxZ), new Vec3(bounds.maxX, bounds.maxY, bounds.maxZ)},
                    {new Vec3(bounds.minX, bounds.minY, bounds.minZ), new Vec3(bounds.minX, bounds.minY, bounds.maxZ)},
                    {new Vec3(bounds.minX, bounds.maxY, bounds.minZ), new Vec3(bounds.minX, bounds.maxY, bounds.maxZ)},
                    {new Vec3(bounds.maxX, bounds.minY, bounds.minZ), new Vec3(bounds.maxX, bounds.minY, bounds.maxZ)},
                    {new Vec3(bounds.maxX, bounds.maxY, bounds.minZ), new Vec3(bounds.maxX, bounds.maxY, bounds.maxZ)}}) {
                if(intersects(line[0], line[1])) {
                    return true;
                }
            }
            return false;
        }

        private boolean intersects(Vec3 start, Vec3 end) {
            Vec3 dir = end.subtract(start).normalize();
            double dot = normal.dot(dir);
            if(dot == 0) {
                // for ease, will always be triggered by another non-parallel line for AABBs
                return false;
            }
            double t = (normal.dot(vertices[0][0]) - normal.dot(start)) / dot;
            Vec3 intersection = start.add(dir.scale(t));
            return inside(intersection);
        }

        private boolean inside(Vec3 point) {
            for(Vec3[] opposites : vertices) {
                if(opposites[0].subtract(point).dot(opposites[1].subtract(point)) >= 0) {
                    return false;
                }
            }
            return true;
        }

        public AABB getBounds() {
            if (bounds == null) {
                double minX = Arrays.stream(vertices).flatMap(Arrays::stream).mapToDouble(Vec3::x).min().orElseThrow();
                double maxX = Arrays.stream(vertices).flatMap(Arrays::stream).mapToDouble(Vec3::x).max().orElseThrow();
                double minY = Arrays.stream(vertices).flatMap(Arrays::stream).mapToDouble(Vec3::y).min().orElseThrow();
                double maxY = Arrays.stream(vertices).flatMap(Arrays::stream).mapToDouble(Vec3::y).max().orElseThrow();
                double minZ = Arrays.stream(vertices).flatMap(Arrays::stream).mapToDouble(Vec3::z).min().orElseThrow();
                double maxZ = Arrays.stream(vertices).flatMap(Arrays::stream).mapToDouble(Vec3::z).max().orElseThrow();
                bounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
            }
            return bounds;
        }

    }

}
