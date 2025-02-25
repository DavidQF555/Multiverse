package multiverse.common.packets;

import multiverse.client.ClientHelper;
import multiverse.common.util.TagUtil;
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

public record RiftExplosionParticlesPacket(Vec3 center,
                                           @Nullable ResourceKey<Level> from) implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, RiftExplosionParticlesPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, packet -> packet.center().x(),
            ByteBufCodecs.DOUBLE, packet -> packet.center().y(),
            ByteBufCodecs.DOUBLE, packet -> packet.center().z(),
            ByteBufCodecs.optional(TagUtil.WORLD_CODEC), packet -> Optional.ofNullable(packet.from()),
            RiftExplosionParticlesPacket::new
    );
    public static final IPayloadHandler<RiftExplosionParticlesPacket> HANDLER = (packet, context) -> ClientHelper.addRiftExplosionParticles(packet.center(), packet.from());

    public RiftExplosionParticlesPacket(double x, double y, double z, Optional<ResourceKey<Level>> from) {
        this(new Vec3(x, y, z), from.orElse(null));
    }

    @Override
    public Type<? extends RiftExplosionParticlesPacket> type() {
        return PacketRegistry.RIFT_EXPLOSION_PARTICLES;
    }

}