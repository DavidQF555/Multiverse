package io.github.davidqf555.minecraft.multiverse.common.packets;

import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RiftExplosionParticlesPacket {

    private static final BiConsumer<RiftExplosionParticlesPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeDouble(message.center.x());
        buffer.writeDouble(message.center.y());
        buffer.writeDouble(message.center.z());
        buffer.writeBoolean(message.from != null);
        if (message.from != null) {
            buffer.writeResourceLocation(message.from.location());
        }
    };
    private static final Function<FriendlyByteBuf, RiftExplosionParticlesPacket> DECODER = buffer -> new RiftExplosionParticlesPacket(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readBoolean() ? ResourceKey.create(Registries.DIMENSION, buffer.readResourceLocation()) : null);
    private static final BiConsumer<RiftExplosionParticlesPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ResourceKey<Level> from;
    private final Vec3 center;

    public RiftExplosionParticlesPacket(Vec3 center, @Nullable ResourceKey<Level> from) {
        this.center = center;
        this.from = from;
    }

    public static void register(int index) {
        Multiverse.CHANNEL.registerMessage(index, RiftExplosionParticlesPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientHelper.addRiftExplosionParticles(center, from));
        context.setPacketHandled(true);
    }

}
