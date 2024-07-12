package io.github.davidqf555.minecraft.multiverse.common.packets;

import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class UpdateClientDimensionsPacket implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, UpdateClientDimensionsPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(loc -> ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(loc)), key -> key.location().toString()), packet -> packet.key,
            UpdateClientDimensionsPacket::new
    );
    public static final IPayloadHandler<UpdateClientDimensionsPacket> HANDLER = (packet, context) -> ClientHelper.addDimension(packet.key);
    private final ResourceKey<Level> key;

    public UpdateClientDimensionsPacket(ResourceKey<Level> key) {
        this.key = key;
    }

    @Override
    public Type<? extends UpdateClientDimensionsPacket> type() {
        return PacketRegistry.UPDATE_DIM;
    }
}