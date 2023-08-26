package io.github.davidqf555.minecraft.multiverse.common.packets;

import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RiftParticlesPacket {

    private static final BiConsumer<RiftParticlesPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeBoolean(message.from.isPresent());
        message.from.ifPresent(buffer::writeInt);
        buffer.writeDouble(message.center.x());
        buffer.writeDouble(message.center.y());
        buffer.writeDouble(message.center.z());
    };
    private static final Function<FriendlyByteBuf, RiftParticlesPacket> DECODER = buffer -> new RiftParticlesPacket(buffer.readBoolean() ? OptionalInt.of(buffer.readInt()) : OptionalInt.empty(), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
    private static final BiConsumer<RiftParticlesPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final OptionalInt from;
    private final Vec3 center;

    public RiftParticlesPacket(OptionalInt from, Vec3 center) {
        this.from = from;
        this.center = center;
    }

    public static void register(int index) {
        Multiverse.CHANNEL.registerMessage(index, RiftParticlesPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientHelper.addRiftParticles(from, center));
        context.setPacketHandled(true);
    }

}
