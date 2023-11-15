package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.common.data.ArrowSummonsData;
import io.github.davidqf555.minecraft.multiverse.common.items.IDeathEffect;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseExistingData;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.BiomesManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.EffectsManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.ShapesManager;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EventBusSubscriber {

    private EventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        event.getGeneration().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Holder.direct(FeatureRegistry.PLACED_RIFT.get()));
        event.getGeneration().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Holder.direct(FeatureRegistry.KALEIDITE_CLUSTER.get()));
        event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityRegistry.TRAVELER.get(), 1, 1, 1));
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        Registry<LevelStem> registry = server.getWorldData().worldGenSettings().dimensions();
        for (int index : MultiverseExistingData.getOrCreate(server).getExisting()) {
            ResourceLocation loc = DimensionHelper.getRegistryKey(index).location();
            if (!registry.containsKey(loc)) {
                Registry.register(registry, loc, DimensionHelper.createDimension(server, index));
            }
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ShapesManager.INSTANCE.load(event.getServer());
        BiomesManager.INSTANCE.load(event.getServer());
        EffectsManager.INSTANCE.load(event.getServer());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.world.isClientSide()) {
            ArrowSummonsData.get((ServerLevel) event.world).ifPresent(data -> data.tick((ServerLevel) event.world));
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        ItemStack main = entity.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack off = entity.getItemInHand(InteractionHand.OFF_HAND);
        if (!main.isEmpty() && main.getItem() instanceof IDeathEffect) {
            if (((IDeathEffect) main.getItem()).onDeath(entity, main)) {
                event.setCanceled(true);
            }
            main.split(1);
            return;
        }
        if (!off.isEmpty() && off.getItem() instanceof IDeathEffect) {
            if (((IDeathEffect) off.getItem()).onDeath(entity, off)) {
                event.setCanceled(true);
            }
            off.split(1);
        }
    }

}
