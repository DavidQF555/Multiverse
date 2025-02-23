package multiverse.common.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class TagUtil {

    public static final StreamCodec<FriendlyByteBuf, ResourceKey<Level>> WORLD_CODEC = new StreamCodec<>() {
        @Override
        public ResourceKey<Level> decode(FriendlyByteBuf buffer) {
            return ResourceKey.create(Registries.DIMENSION, buffer.readResourceLocation());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, ResourceKey<Level> value) {
            buffer.writeResourceLocation(value.location());
        }
    };

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
