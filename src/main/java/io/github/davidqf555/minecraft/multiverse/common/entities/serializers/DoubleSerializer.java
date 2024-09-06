package io.github.davidqf555.minecraft.multiverse.common.entities.serializers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class DoubleSerializer implements EntityDataSerializer<Double> {

    public static final DoubleSerializer INSTANCE = new DoubleSerializer();

    protected DoubleSerializer(){}

    @Override
    public void write(FriendlyByteBuf buffer, Double val) {
        buffer.writeDouble(val);
    }

    @Override
    public Double read(FriendlyByteBuf buffer) {
        return buffer.readDouble();
    }

    @Override
    public Double copy(Double val) {
        return val;
    }

}
