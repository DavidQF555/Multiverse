package multiverse.common.events;

import com.mojang.serialization.Lifecycle;
import multiverse.common.Multiverse;
import multiverse.common.ServerConfigs;
import multiverse.common.packets.RiftEffectPacket;
import multiverse.common.util.MultiverseConfig;
import multiverse.common.world.ArrowSummonsData;
import multiverse.common.world.worldgen.ShapesReader;
import multiverse.common.world.worldgen.TargetDimensionsReader;
import multiverse.common.world.worldgen.generators.GeneratorHelper;
import multiverse.common.world.worldgen.generators.ShapeDimensionGenerator;
import multiverse.registration.AttachmentTypeRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
    public static void onServerAboutToStartHigh(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        ShapesReader shapes = new ShapesReader(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "shapes.json"));
        shapes.load(server);
        ShapeDimensionGenerator provider = new ShapeDimensionGenerator(shapes.getShapes());
        RegistryAccess.Frozen composite = server.registries().compositeAccess();
        MappedRegistry<LevelStem> registry = (MappedRegistry<LevelStem>) composite.lookupOrThrow(Registries.LEVEL_STEM);
        registry.unfreeze(true);
        long seed = server.getWorldData().worldGenOptions().seed();
        for (int i = 1; i <= ServerConfigs.INSTANCE.generated.get(); i++) {
            ResourceKey<LevelStem> key = ResourceKey.create(Registries.LEVEL_STEM, GeneratorHelper.getResourceLocation(i));
            if (!registry.containsKey(key)) {
                registry.register(key, provider.createDimension(server.registryAccess(), seed, i), new RegistrationInfo(Optional.empty(), Lifecycle.experimental()));
            }
        }
        registry.freeze();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onServerAboutToStartLow(ServerAboutToStartEvent event) {
        TargetDimensionsReader targets = new TargetDimensionsReader(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "targets.json"));
        targets.load(event.getServer());
        MultiverseConfig.setTargetDimensions(targets.getDimensions().stream().map(key -> ResourceKey.create(Registries.DIMENSION, key.location())).toList());
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
