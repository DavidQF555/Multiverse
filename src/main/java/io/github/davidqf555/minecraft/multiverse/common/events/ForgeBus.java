package io.github.davidqf555.minecraft.multiverse.common.events;

import com.mojang.serialization.Lifecycle;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import io.github.davidqf555.minecraft.multiverse.common.world.ArrowSummonsData;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.capabilities.NBTCapabilityProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.capabilities.SummonedData;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.ShapesManager;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.ShapeDimensionProvider;
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
    @SubscribeEvent
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
                registry.register(key, ShapeDimensionProvider.INSTANCE.createDimension(server.registryAccess(), seed, i), Lifecycle.experimental());
            }
        }
        registry.freeze();
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
        if (!event.isCanceled() && entity instanceof Mob && !entity.level.isClientSide() && SummonedData.isSummoned((Mob) entity)) {
            Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new RiftParticlesPacket(entity.getEyePosition(), null));
            entity.discard();
        }
    }

}
