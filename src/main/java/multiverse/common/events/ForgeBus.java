package multiverse.common.events;

import multiverse.common.Multiverse;
import multiverse.common.packets.RiftEffectPacket;
import multiverse.common.world.ArrowSummonsData;
import multiverse.common.world.DimensionHelper;
import multiverse.common.world.capabilities.NBTCapabilityProvider;
import multiverse.common.world.capabilities.SummonedData;
import multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeBus {

    private static final ResourceLocation SUMMONED_DATA = new ResourceLocation(Multiverse.MOD_ID, "summoned");

    private ForgeBus() {
    }

    @SubscribeEvent
    public static void onRaiderAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Raider) {
            event.addCapability(SUMMONED_DATA, new NBTCapabilityProvider<>(SummonedData.CAPABILITY, new SummonedData()));
        }
    }

    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        event.getGeneration().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Holder.direct(FeatureRegistry.PLACED_RIFT.get()));
        event.getGeneration().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Holder.direct(FeatureRegistry.KALEIDITE_CLUSTER.get()));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onServerAboutToStart(ServerAboutToStartEvent event) throws IOException {
        DimensionHelper.loadTargetDimensions(event.getServer(), new ResourceLocation(Multiverse.MOD_ID, "targets.json"));
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.world.isClientSide()) {
            ArrowSummonsData.get((ServerLevel) event.world).ifPresent(data -> data.tick((ServerLevel) event.world));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!event.isCanceled() && entity instanceof Mob && !entity.level.isClientSide() && SummonedData.isSummoned((Mob) entity)) {
            Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new RiftEffectPacket(entity.getEyePosition(), entity.getSoundSource(), null));
            entity.discard();
        }
    }

}
