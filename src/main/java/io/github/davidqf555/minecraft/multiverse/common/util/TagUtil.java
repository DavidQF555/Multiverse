package io.github.davidqf555.minecraft.multiverse.common.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class TagUtil {

    public static final StreamCodec<ByteBuf, ResourceKey<Level>> WORLD_CODEC = ByteBufCodecs.STRING_UTF8.map(val -> ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(val)), key -> key.location().toString());

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
