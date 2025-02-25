package multiverse.common.packets;

import multiverse.client.ClientHelper;
import multiverse.common.Multiverse;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record RiftEffectPacket(Vec3 center, SoundSource source, @Nullable ResourceKey<Level> from) {

    private static final BiConsumer<RiftEffectPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeDouble(message.center().x());
        buffer.writeDouble(message.center().y());
        buffer.writeDouble(message.center().z());
        buffer.writeEnum(message.source());
        buffer.writeBoolean(message.from() != null);
        if (message.from() != null) {
            buffer.writeResourceLocation(message.from().location());
        }
    };
    private static final Function<FriendlyByteBuf, RiftEffectPacket> DECODER = buffer -> new RiftEffectPacket(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readEnum(SoundSource.class), buffer.readBoolean() ? ResourceKey.create(Registry.DIMENSION_REGISTRY, buffer.readResourceLocation()) : null);
    private static final BiConsumer<RiftEffectPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        cont.enqueueWork(() -> {
            ClientHelper.addRiftParticles(message.center(), message.from());
            ClientHelper.playWarpSound(message.center(), message.source());
        });
        cont.setPacketHandled(true);
    };

    public static void register(int index) {
        Multiverse.CHANNEL.registerMessage(index, RiftEffectPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

}
