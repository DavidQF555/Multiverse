package io.github.davidqf555.minecraft.multiverse.common.packets;

import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.Optional;

public class RiftParticlesPacket implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, RiftParticlesPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.INT), packet -> packet.from,
            ByteBufCodecs.DOUBLE, packet -> packet.x,
            ByteBufCodecs.DOUBLE, packet -> packet.y,
            ByteBufCodecs.DOUBLE, packet -> packet.z,
            RiftParticlesPacket::new
    );
    public static final IPayloadHandler<RiftParticlesPacket> HANDLER = (packet, context) -> ClientHelper.addRiftParticles(packet.from, new Vec3(packet.x, packet.y, packet.z));
    private final Optional<Integer> from;
    private final double x, y, z;

    public RiftParticlesPacket(Optional<Integer> from, Vec3 loc) {
        this(from, loc.x(), loc.y(), loc.z());
    }

    public RiftParticlesPacket(Optional<Integer> from, double x, double y, double z) {
        this.from = from;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Type<? extends RiftParticlesPacket> type() {
        return PacketRegistry.RIFT_PARTICLES;
    }

}
