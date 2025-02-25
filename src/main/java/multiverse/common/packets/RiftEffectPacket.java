package multiverse.common.packets;

import multiverse.client.ClientHelper;
import multiverse.common.util.TagUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class RiftEffectPacket implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, RiftEffectPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, packet -> packet.x,
            ByteBufCodecs.DOUBLE, packet -> packet.y,
            ByteBufCodecs.DOUBLE, packet -> packet.z,
            NeoForgeStreamCodecs.enumCodec(SoundSource.class), packet -> packet.source,
            ByteBufCodecs.optional(TagUtil.WORLD_CODEC), packet -> Optional.ofNullable(packet.from),
            RiftEffectPacket::new
    );
    public static final IPayloadHandler<RiftEffectPacket> HANDLER = (packet, context) -> {
        Vec3 pos = new Vec3(packet.x, packet.y, packet.z);
        ClientHelper.addRiftParticles(pos, packet.from);
        ClientHelper.playWarpSound(pos, packet.source);
    };
    private final ResourceKey<Level> from;
    private final SoundSource source;
    private final double x, y, z;

    public RiftEffectPacket(Vec3 loc, SoundSource source, @Nullable ResourceKey<Level> from) {
        this(loc.x(), loc.y(), loc.z(), source, from);
    }

    public RiftEffectPacket(double x, double y, double z, SoundSource source, Optional<ResourceKey<Level>> from) {
        this(x, y, z, source, from.orElse(null));
    }

    public RiftEffectPacket(double x, double y, double z, SoundSource source, @Nullable ResourceKey<Level> from) {
        this.from = from;
        this.source = source;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Type<? extends RiftEffectPacket> type() {
        return PacketRegistry.RIFT_EFFECT;
    }

}
