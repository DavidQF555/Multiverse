package io.github.davidqf555.minecraft.multiverse.common.packets;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientDimensionsPacket {

    private static final BiConsumer<UpdateClientDimensionsPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> buffer.writeUtf(message.key.location().toString());
    private static final Function<FriendlyByteBuf, UpdateClientDimensionsPacket> DECODER = buffer -> new UpdateClientDimensionsPacket(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(buffer.readUtf())));
    private static final BiConsumer<UpdateClientDimensionsPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ResourceKey<Level> key;

    public UpdateClientDimensionsPacket(ResourceKey<Level> key) {
        this.key = key;
    }

    public static void register(int index) {
        Multiverse.CHANNEL.registerMessage(index, UpdateClientDimensionsPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> Minecraft.getInstance().player.connection.levels().add(key));
            context.setPacketHandled(true);
        }
    }

}