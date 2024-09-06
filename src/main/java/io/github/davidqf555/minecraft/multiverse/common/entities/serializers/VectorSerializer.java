package io.github.davidqf555.minecraft.multiverse.common.entities.serializers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;

public class VectorSerializer implements EntityDataSerializer<Vec3> {

    public static final VectorSerializer INSTANCE = new VectorSerializer();

    protected VectorSerializer() {
    }

    @Override
    public void write(FriendlyByteBuf buffer, Vec3 val) {
        buffer.writeDouble(val.x());
        buffer.writeDouble(val.y());
        buffer.writeDouble(val.z());
    }

    @Override
    public Vec3 read(FriendlyByteBuf buffer) {
        return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public Vec3 copy(Vec3 val) {
        return new Vec3(val.x(), val.y(), val.z());
    }

}
