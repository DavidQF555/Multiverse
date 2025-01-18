package io.github.davidqf555.minecraft.multiverse.common.packets;

import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.common.util.TagUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.Optional;

public class RiftExplosionParticlesPacket implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, RiftExplosionParticlesPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(TagUtil.WORLD_CODEC), packet -> packet.from,
            ByteBufCodecs.DOUBLE, packet -> packet.x,
            ByteBufCodecs.DOUBLE, packet -> packet.y,
            ByteBufCodecs.DOUBLE, packet -> packet.z,
            RiftExplosionParticlesPacket::new
    );
    public static final IPayloadHandler<RiftExplosionParticlesPacket> HANDLER = (packet, context) -> ClientHelper.addRiftExplosionParticles(packet.from, new Vec3(packet.x, packet.y, packet.z));
    private final Optional<ResourceKey<Level>> from;
    private final double x, y, z;

    public RiftExplosionParticlesPacket(Optional<ResourceKey<Level>> from, Vec3 loc) {
        this(from, loc.x(), loc.y(), loc.z());
    }

    public RiftExplosionParticlesPacket(Optional<ResourceKey<Level>> from, double x, double y, double z) {
        this.from = from;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Type<? extends RiftExplosionParticlesPacket> type() {
        return PacketRegistry.RIFT_EXPLOSION_PARTICLES;
    }

}
