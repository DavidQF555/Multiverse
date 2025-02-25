package multiverse.common.packets;

import multiverse.client.colors.MultiverseColorHelper;
import multiverse.common.Multiverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record UpdateColorSeedPacket(long seed) {

    private static final BiConsumer<UpdateColorSeedPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> buffer.writeLong(message.seed());
    private static final Function<FriendlyByteBuf, UpdateColorSeedPacket> DECODER = buffer -> new UpdateColorSeedPacket(buffer.readLong());
    private static final BiConsumer<UpdateColorSeedPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        cont.enqueueWork(() -> MultiverseColorHelper.setBaseSeed(message.seed()));
        cont.setPacketHandled(true);
    };

    public static void register(int index) {
        Multiverse.CHANNEL.registerMessage(index, UpdateColorSeedPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

}
