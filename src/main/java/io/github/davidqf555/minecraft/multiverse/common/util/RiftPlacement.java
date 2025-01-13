package io.github.davidqf555.minecraft.multiverse.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record RiftPlacement(Vec3 center, double width, double height, Vec3 normal, float angle) {

    @Nullable
    public static RiftPlacement deserialize(CompoundTag tag) {
        if (tag.contains("Center", Tag.TAG_COMPOUND) && tag.contains("Width", Tag.TAG_DOUBLE) && tag.contains("Height", Tag.TAG_DOUBLE) && tag.contains("Normal", Tag.TAG_COMPOUND) && tag.contains("Angle", Tag.TAG_FLOAT)) {
            Vec3 center = TagUtil.readVec(tag.getCompound("Center"));
            if (center == null) {
                return null;
            }
            Vec3 normal = TagUtil.readVec(tag.getCompound("Normal"));
            if (normal == null) {
                return null;
            }
            normal = normal.normalize();
            if (normal.lengthSqr() == 0) {
                normal = new Vec3(0, 1, 0);
            }
            double width = tag.getDouble("Width");
            double height = tag.getDouble("Height");
            float angle = tag.getFloat("Angle");
            return new RiftPlacement(center, width, height, normal, angle);
        }
        return null;
    }

    public Vec3[][] calculateLayers(BlockPos pos, int layers) {
        Vec3[][] out = new Vec3[layers][];
        for (int i = 0; i < layers; i++) {
            double width = (i + 1) * width() / layers;
            double height = (i + 1) * height() / layers;
            RiftPlacement child = new RiftPlacement(center(), width, height, normal(), angle());
            out[i] = RiftPlacementHelper.calculateVerticesAt(child, pos);
        }
        return out;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.put("Center", TagUtil.writeVec(center()));
        tag.putDouble("Width", width());
        tag.putDouble("Height", height());
        tag.put("Normal", TagUtil.writeVec(normal()));
        tag.putFloat("Angle", angle());
        return tag;
    }

}
