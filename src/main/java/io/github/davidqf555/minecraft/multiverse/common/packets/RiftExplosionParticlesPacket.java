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

import javax.annotation.Nullable;
import java.util.Optional;

public class RiftExplosionParticlesPacket implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, RiftExplosionParticlesPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, packet -> packet.x,
            ByteBufCodecs.DOUBLE, packet -> packet.y,
            ByteBufCodecs.DOUBLE, packet -> packet.z,
            ByteBufCodecs.optional(TagUtil.WORLD_CODEC), packet -> Optional.ofNullable(packet.from),
            RiftExplosionParticlesPacket::new
    );
    public static final IPayloadHandler<RiftExplosionParticlesPacket> HANDLER = (packet, context) -> ClientHelper.addRiftExplosionParticles(new Vec3(packet.x, packet.y, packet.z), packet.from);
    private final ResourceKey<Level> from;
    private final double x, y, z;

    public RiftExplosionParticlesPacket(Vec3 loc, @Nullable ResourceKey<Level> from) {
        this(loc.x(), loc.y(), loc.z(), from);
    }

    public RiftExplosionParticlesPacket(double x, double y, double z, Optional<ResourceKey<Level>> from) {
        this(x, y, z, from.orElse(null));
    }

    public RiftExplosionParticlesPacket(double x, double y, double z, @Nullable ResourceKey<Level> from) {
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
