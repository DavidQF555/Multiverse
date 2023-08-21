package io.github.davidqf555.minecraft.multiverse.common.packets;

import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RiftParticlesPacket {

    private static final BiConsumer<RiftParticlesPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeDouble(message.center.x());
        buffer.writeDouble(message.center.y());
        buffer.writeDouble(message.center.z());
        buffer.writeDouble(message.centerVariation);
        buffer.writeInt(message.count);
    };
    private static final Function<FriendlyByteBuf, RiftParticlesPacket> DECODER = buffer -> new RiftParticlesPacket(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readDouble(), buffer.readInt());
    private static final BiConsumer<RiftParticlesPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final Vec3 center;
    private final double centerVariation;
    private final int count;

    public RiftParticlesPacket(Vec3 center, double centerVariation, int count) {
        this.center = center;
        this.centerVariation = centerVariation;
        this.count = count;
    }

    public static void register(int index) {
        Multiverse.CHANNEL.registerMessage(index, RiftParticlesPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientHelper.addRiftParticles(center, centerVariation, count));
        context.setPacketHandled(true);
    }

}
