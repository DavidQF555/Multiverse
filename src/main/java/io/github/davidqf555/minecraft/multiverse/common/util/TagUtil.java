package io.github.davidqf555.minecraft.multiverse.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class TagUtil {

    private TagUtil() {
    }

    public static CompoundTag writeVec(Vec3 vec) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("X", vec.x());
        tag.putDouble("Y", vec.y());
        tag.putDouble("Z", vec.z());
        return tag;
    }

    @Nullable
    public static Vec3 readVec(CompoundTag tag) {
        if (tag.contains("X", Tag.TAG_DOUBLE) && tag.contains("Y", Tag.TAG_DOUBLE) && tag.contains("Z", Tag.TAG_DOUBLE)) {
            return new Vec3(tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z"));
        }
        return null;
    }

}
