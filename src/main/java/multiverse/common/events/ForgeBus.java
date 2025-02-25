package multiverse.common.events;

import multiverse.common.Multiverse;
import multiverse.common.packets.RiftEffectPacket;
import multiverse.common.packets.UpdateColorSeedPacket;
import multiverse.common.world.ArrowSummonsData;
import multiverse.common.world.DimensionHelper;
import multiverse.common.world.capabilities.NBTCapabilityProvider;
import multiverse.common.world.capabilities.SummonedData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
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
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel world = player.getServer().getLevel(Level.OVERWORLD);
        long seed = world == null ? 0 : BiomeManager.obfuscateSeed(world.getSeed());
        Multiverse.CHANNEL.send(new UpdateColorSeedPacket(seed), PacketDistributor.PLAYER.with(player));
    }

    @SubscribeEvent
    public static void onRaiderAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Raider) {
            event.addCapability(SUMMONED_DATA, new NBTCapabilityProvider<>(SummonedData.CAPABILITY, new SummonedData()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerAboutToStart(ServerAboutToStartEvent event) throws IOException {
        DimensionHelper.loadTargetDimensions(event.getServer(), new ResourceLocation(Multiverse.MOD_ID, "targets.json"));
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
            Multiverse.CHANNEL.send(new RiftEffectPacket(entity.getEyePosition(), entity.getSoundSource(), null), PacketDistributor.TRACKING_ENTITY.with(entity));
            entity.discard();
        }
    }

}
