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

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RiftExplosionParticlesPacket {

    private static final BiConsumer<RiftExplosionParticlesPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeBoolean(message.from.isPresent());
        message.from.map(ResourceKey::location).ifPresent(buffer::writeResourceLocation);
        buffer.writeDouble(message.center.x());
        buffer.writeDouble(message.center.y());
        buffer.writeDouble(message.center.z());
    };
    private static final Function<FriendlyByteBuf, RiftExplosionParticlesPacket> DECODER = buffer -> new RiftExplosionParticlesPacket(buffer.readBoolean() ? Optional.of(ResourceKey.create(Registries.DIMENSION, buffer.readResourceLocation())) : Optional.empty(), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
    private static final BiConsumer<RiftExplosionParticlesPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final Optional<ResourceKey<Level>> from;
    private final Vec3 center;

    public RiftExplosionParticlesPacket(Optional<ResourceKey<Level>> from, Vec3 center) {
        this.from = from;
        this.center = center;
    }

    public static void register(int index) {
        Multiverse.CHANNEL.registerMessage(index, RiftExplosionParticlesPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientHelper.addRiftExplosionParticles(from, center));
        context.setPacketHandled(true);
    }

}
