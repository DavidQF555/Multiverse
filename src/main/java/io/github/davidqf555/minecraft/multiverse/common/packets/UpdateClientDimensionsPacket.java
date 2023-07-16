package io.github.davidqf555.minecraft.multiverse.common.packets;

import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientDimensionsPacket {

    private static final BiConsumer<UpdateClientDimensionsPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> buffer.writeUtf(message.key.location().toString());
    private static final Function<FriendlyByteBuf, UpdateClientDimensionsPacket> DECODER = buffer -> new UpdateClientDimensionsPacket(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buffer.readUtf())));
    private static final BiConsumer<UpdateClientDimensionsPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ResourceKey<Level> key;

    public UpdateClientDimensionsPacket(ResourceKey<Level> key) {
        this.key = key;
    }

    public static void register(int index) {
        Multiverse.CHANNEL.registerMessage(index, UpdateClientDimensionsPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientHelper.addDimension(key));
        context.setPacketHandled(true);
    }

}