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
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.dimension.LevelStem;
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerAboutToStartHigh(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        ShapesReader shapes = new ShapesReader(new ResourceLocation(Multiverse.MOD_ID, "shapes.json"));
        shapes.load(server);
        ShapeDimensionGenerator provider = new ShapeDimensionGenerator(shapes.getShapes());
        WritableRegistry<LevelStem> registry = (WritableRegistry<LevelStem>) server.getWorldData().worldGenSettings().dimensions();
        long seed = server.getWorldData().worldGenSettings().seed();
        for (int i = 1; i <= ServerConfigs.INSTANCE.generated.get(); i++) {
            ResourceKey<LevelStem> key = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, GeneratorHelper.getResourceLocation(i));
            if (!registry.containsKey(key)) {
                registry.register(key, provider.createDimension(server.registryAccess(), seed, i), Lifecycle.experimental());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onServerAboutToStartLow(ServerAboutToStartEvent event) {
        TargetDimensionsReader targets = new TargetDimensionsReader(new ResourceLocation(Multiverse.MOD_ID, "targets.json"));
        targets.load(event.getServer());
        MultiverseConfig.setTargetDimensions(targets.getDimensions().stream().map(key -> ResourceKey.create(Registry.DIMENSION_REGISTRY, key.location())).toList());
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
            Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new RiftParticlesPacket(entity.getEyePosition(), null));
            entity.discard();
        }
    }

}
