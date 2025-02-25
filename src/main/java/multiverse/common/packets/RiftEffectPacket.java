package multiverse.common.packets;

import multiverse.client.ClientHelper;
import multiverse.common.Multiverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
    private static final Function<FriendlyByteBuf, RiftEffectPacket> DECODER = buffer -> new RiftEffectPacket(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readEnum(SoundSource.class), buffer.readBoolean() ? ResourceKey.create(Registries.DIMENSION, buffer.readResourceLocation()) : null);

    public static void register(int index) {
        Multiverse.CHANNEL.messageBuilder(RiftEffectPacket.class, index, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(RiftEffectPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ClientHelper.addRiftParticles(center, from);
        ClientHelper.playWarpSound(center, source);
    }

}
