package multiverse.common.packets;

import io.netty.buffer.ByteBuf;
import multiverse.client.colors.MultiverseColorHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record UpdateColorSeedPacket(long seed) implements CustomPacketPayload {

    public static final StreamCodec<ByteBuf, UpdateColorSeedPacket> CODEC = ByteBufCodecs.VAR_LONG.map(UpdateColorSeedPacket::new, UpdateColorSeedPacket::seed);
    public static final IPayloadHandler<UpdateColorSeedPacket> HANDLER = (packet, context) -> MultiverseColorHelper.setBaseSeed(packet.seed());

    @Override
    public CustomPacketPayload.Type<? extends UpdateColorSeedPacket> type() {
        return PacketRegistry.UPDATE_COLOR_SEED;
    }

}
