package multiverse.common.packets;

import multiverse.client.colors.MultiverseColorHelper;
import multiverse.common.Multiverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record UpdateColorSeedPacket(long seed) {

    private static final BiConsumer<UpdateColorSeedPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> buffer.writeLong(message.seed());
    private static final Function<FriendlyByteBuf, UpdateColorSeedPacket> DECODER = buffer -> new UpdateColorSeedPacket(buffer.readLong());

    public static void register(int index) {
        Multiverse.CHANNEL.messageBuilder(UpdateColorSeedPacket.class, index, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(UpdateColorSeedPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        MultiverseColorHelper.setBaseSeed(seed());
    }

}
