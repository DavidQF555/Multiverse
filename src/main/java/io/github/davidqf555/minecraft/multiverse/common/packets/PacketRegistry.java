package io.github.davidqf555.minecraft.multiverse.common.packets;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class PacketRegistry {

    public static final CustomPacketPayload.Type<RiftParticlesPacket> RIFT_PARTICLES = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "rift_particles"));
    public static final CustomPacketPayload.Type<UpdateClientDimensionsPacket> UPDATE_DIM = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "update_dimensions"));

    private PacketRegistry() {
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(
                        RIFT_PARTICLES,
                        RiftParticlesPacket.CODEC,
                        RiftParticlesPacket.HANDLER
                )
                .playToClient(
                        UPDATE_DIM,
                        UpdateClientDimensionsPacket.CODEC,
                        UpdateClientDimensionsPacket.HANDLER
                );
    }

}
