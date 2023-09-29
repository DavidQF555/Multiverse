package io.github.davidqf555.minecraft.multiverse.common.packets;

import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class UpdateClientDimensionsPacket {

    private static final BiConsumer<UpdateClientDimensionsPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> buffer.writeUtf(message.key.location().toString());
    private static final Function<FriendlyByteBuf, UpdateClientDimensionsPacket> DECODER = buffer -> new UpdateClientDimensionsPacket(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buffer.readUtf())));

    private final ResourceKey<Level> key;

    public UpdateClientDimensionsPacket(ResourceKey<Level> key) {
        this.key = key;
    }

    public static void register(int index) {
        Multiverse.CHANNEL.messageBuilder(UpdateClientDimensionsPacket.class, index, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(UpdateClientDimensionsPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ClientHelper.addDimension(key);
        context.setPacketHandled(true);
    }

}