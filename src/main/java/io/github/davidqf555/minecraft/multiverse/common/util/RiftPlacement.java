package io.github.davidqf555.minecraft.multiverse.common.util;

import io.github.davidqf555.minecraft.multiverse.client.ClientConfigs;
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

    private static double getSizeFactor(int layer, int layers) {
        if (layers <= 1) {
            return 1;
        }
        double rate = ClientConfigs.INSTANCE.riftLayerGrowth.get();
        double start = ClientConfigs.INSTANCE.riftLayerStart.get();
        double denom = 1 - Math.pow(rate, layers - 1);
        if (denom == 0) {
            return start + (1 - start) * layer / (layers - 1);
        }
        double inter = (1 - start * Math.pow(rate, layers - 1)) / denom;
        return -(inter - start) * Math.pow(rate, layer) + inter;
    }

    public Vec3[][] calculateLayers(BlockPos pos) {
        int layers = ClientConfigs.INSTANCE.riftLayers.get();
        Vec3[][] out = new Vec3[layers][];
        for (int i = 0; i < layers; i++) {
            double factor = getSizeFactor(i, layers);
            RiftPlacement child = new RiftPlacement(center(), width() * factor, height() * factor, normal(), angle());
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
