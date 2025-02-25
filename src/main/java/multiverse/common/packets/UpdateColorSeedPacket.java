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

public class UpdateColorSeedPacket {

    private static final BiConsumer<UpdateColorSeedPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> buffer.writeLong(message.seed);
    private static final Function<FriendlyByteBuf, UpdateColorSeedPacket> DECODER = buffer -> new UpdateColorSeedPacket(buffer.readLong());
    private static final BiConsumer<UpdateColorSeedPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final long seed;

    public UpdateColorSeedPacket(long seed) {
        this.seed = seed;
    }

    public static void register(int index) {
        Multiverse.CHANNEL.registerMessage(index, UpdateColorSeedPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> MultiverseColorHelper.setBaseSeed(seed));
        context.setPacketHandled(true);
    }

}
