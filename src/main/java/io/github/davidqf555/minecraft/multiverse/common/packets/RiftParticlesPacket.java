package io.github.davidqf555.minecraft.multiverse.common.packets;

import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RiftParticlesPacket {

    private static final BiConsumer<RiftParticlesPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeBoolean(message.from.isPresent());
        message.from.ifPresent(buffer::writeResourceKey);
        buffer.writeDouble(message.center.x());
        buffer.writeDouble(message.center.y());
        buffer.writeDouble(message.center.z());
    };
    private static final Function<FriendlyByteBuf, RiftParticlesPacket> DECODER = buffer -> new RiftParticlesPacket(buffer.readBoolean() ? Optional.of(buffer.readResourceKey(Registries.DIMENSION)) : Optional.empty(), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));

    private final Optional<ResourceKey<Level>> from;
    private final Vec3 center;

    public RiftParticlesPacket(Optional<ResourceKey<Level>> from, Vec3 center) {
        this.from = from;
        this.center = center;
    }

    public static void register(int index) {
        Multiverse.CHANNEL.messageBuilder(RiftParticlesPacket.class, index, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(RiftParticlesPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ClientHelper.addRiftParticles(from, center);
    }

}
