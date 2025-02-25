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

public record RiftEffectPacket(Vec3 center, SoundSource source,
                               @Nullable ResourceKey<Level> from) implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, RiftEffectPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, packet -> packet.center().x(),
            ByteBufCodecs.DOUBLE, packet -> packet.center().y(),
            ByteBufCodecs.DOUBLE, packet -> packet.center().z(),
            NeoForgeStreamCodecs.enumCodec(SoundSource.class), RiftEffectPacket::source,
            ByteBufCodecs.optional(TagUtil.WORLD_CODEC), packet -> Optional.ofNullable(packet.from()),
            RiftEffectPacket::new
    );
    public static final IPayloadHandler<RiftEffectPacket> HANDLER = (packet, context) -> {
        ClientHelper.addRiftParticles(packet.center(), packet.from());
        ClientHelper.playWarpSound(packet.center(), packet.source());
    };

    public RiftEffectPacket(double x, double y, double z, SoundSource source, Optional<ResourceKey<Level>> from) {
        this(new Vec3(x, y, z), source, from.orElse(null));
    }

    @Override
    public Type<? extends RiftEffectPacket> type() {
        return PacketRegistry.RIFT_EFFECT;
    }

}
