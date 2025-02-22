package io.github.davidqf555.minecraft.multiverse.common.events;

import com.mojang.serialization.Lifecycle;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import io.github.davidqf555.minecraft.multiverse.common.util.MultiverseConfig;
import io.github.davidqf555.minecraft.multiverse.common.world.ArrowSummonsData;
import io.github.davidqf555.minecraft.multiverse.common.world.capabilities.NBTCapabilityProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.capabilities.SummonedData;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.ShapesReader;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.TargetDimensionsReader;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.GeneratorHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.ShapeDimensionGenerator;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;

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

    // dynamic registering dimensions
    @SuppressWarnings("deprecation")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerAboutToStartHigh(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        ShapesReader shapes = new ShapesReader(new ResourceLocation(Multiverse.MOD_ID, "shapes.json"));
        shapes.load(server);
        ShapeDimensionGenerator provider = new ShapeDimensionGenerator(shapes.getShapes());
        RegistryAccess.Frozen composite = server.registries().compositeAccess();
        MappedRegistry<LevelStem> registry = (MappedRegistry<LevelStem>) composite.registryOrThrow(Registries.LEVEL_STEM);
        registry.unfreeze();
        long seed = server.getWorldData().worldGenOptions().seed();
        for (int i = 1; i <= ServerConfigs.INSTANCE.generated.get(); i++) {
            ResourceKey<LevelStem> key = ResourceKey.create(Registries.LEVEL_STEM, GeneratorHelper.getResourceLocation(i));
            if (!registry.containsKey(key)) {
                registry.register(key, provider.createDimension(server.registryAccess(), seed, i), Lifecycle.experimental());
            }
        }
        registry.freeze();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onServerAboutToStartLow(ServerAboutToStartEvent event) {
        TargetDimensionsReader targets = new TargetDimensionsReader(new ResourceLocation(Multiverse.MOD_ID, "targets.json"));
        targets.load(event.getServer());
        MultiverseConfig.setTargetDimensions(targets.getDimensions().stream().map(key -> ResourceKey.create(Registries.DIMENSION, key.location())).toList());
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.level.isClientSide()) {
            ArrowSummonsData.get((ServerLevel) event.level).ifPresent(data -> data.tick((ServerLevel) event.level));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!event.isCanceled() && entity instanceof Mob && !entity.level().isClientSide() && SummonedData.isSummoned((Mob) entity)) {
            Multiverse.CHANNEL.send(new RiftParticlesPacket(entity.getEyePosition(), null), PacketDistributor.TRACKING_ENTITY.with(entity));
            entity.discard();
        }
    }

}
