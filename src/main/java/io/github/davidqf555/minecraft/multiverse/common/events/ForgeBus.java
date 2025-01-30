package io.github.davidqf555.minecraft.multiverse.common.events;

import com.mojang.serialization.Lifecycle;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import io.github.davidqf555.minecraft.multiverse.common.world.ArrowSummonsData;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.ShapesManager;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.ShapeDimensionProvider;
import io.github.davidqf555.minecraft.multiverse.registration.AttachmentTypeRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ForgeBus {

    private ForgeBus() {
    }

    // dynamic registering dimensions
    @SuppressWarnings("deprecation")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        ShapesManager.INSTANCE.load(server);

        RegistryAccess.Frozen composite = server.registries().compositeAccess();
        MappedRegistry<LevelStem> registry = (MappedRegistry<LevelStem>) composite.registryOrThrow(Registries.LEVEL_STEM);
        registry.unfreeze();
        long seed = server.getWorldData().worldGenOptions().seed();
        for (int i = 1; i <= ServerConfigs.INSTANCE.maxDimensions.get(); i++) {
            ResourceKey<LevelStem> key = ResourceKey.create(Registries.LEVEL_STEM, DimensionHelper.getResourceLocation(i));
            if (!registry.containsKey(key)) {
                registry.register(key, ShapeDimensionProvider.INSTANCE.createDimension(server.registryAccess(), seed, i), new RegistrationInfo(Optional.empty(), Lifecycle.experimental()));
            }
        }
        registry.freeze();
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
            PacketDistributor.sendToPlayersTrackingEntity(entity, new RiftParticlesPacket(entity.getEyePosition(), null));
            entity.discard();
        }
    }

}
