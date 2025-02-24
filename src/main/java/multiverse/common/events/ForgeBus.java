package multiverse.common.events;

import multiverse.common.Multiverse;
import multiverse.common.packets.RiftEffectPacket;
import multiverse.common.util.MultiverseConfig;
import multiverse.common.world.ArrowSummonsData;
import multiverse.common.world.TargetDimensionsReader;
import multiverse.registration.AttachmentTypeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ForgeBus {

    private ForgeBus() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onServerAboutToStartLow(ServerAboutToStartEvent event) {
        TargetDimensionsReader targets = new TargetDimensionsReader(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "targets.json"));
        targets.load(event.getServer());
        MultiverseConfig.setTargetDimensions(targets.getDimensions().stream().map(loc -> ResourceKey.create(Registries.DIMENSION, loc)).toList());
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Pre event) {
        Level level = event.getLevel();
        if (!level.isClientSide()) {
            ArrowSummonsData.get((ServerLevel) level).ifPresent(data -> data.tick((ServerLevel) level));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!event.isCanceled() && entity instanceof Mob && !entity.level().isClientSide() && entity.getData(AttachmentTypeRegistry.SUMMONED)) {
            PacketDistributor.sendToPlayersTrackingEntity(entity, new RiftEffectPacket(entity.getEyePosition(), entity.getSoundSource(), null));
            entity.discard();
        }
    }

}
