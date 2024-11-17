package io.github.davidqf555.minecraft.multiverse.common.events;

import com.mojang.serialization.Lifecycle;
import io.github.davidqf555.minecraft.multiverse.common.ArrowSummonsData;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.integration.TerraBlenderBiomes;
import io.github.davidqf555.minecraft.multiverse.common.items.IDeathEffect;
import io.github.davidqf555.minecraft.multiverse.common.util.ConfigHelper;
import io.github.davidqf555.minecraft.multiverse.common.util.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.ShapesManager;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeBus {

    private ForgeBus() {
    }

    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        event.getGeneration().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Holder.direct(FeatureRegistry.PLACED_RIFT.get()));
        event.getGeneration().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Holder.direct(FeatureRegistry.KALEIDITE_CLUSTER.get()));
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        ShapesManager.INSTANCE.load(event.getServer());
    }

    // a bit hacky, but needs to register after TerraBlender and before levels are created
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        LevelAccessor world = event.getWorld();
        if (world instanceof ServerLevel && ((ServerLevel) world).dimension().equals(Level.OVERWORLD)) {
            if (ModList.get().isLoaded("terrablender")) {
                ConfigHelper.biomes = new TerraBlenderBiomes(world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));
            }
            WritableRegistry<LevelStem> registry = (WritableRegistry<LevelStem>) world.getServer().getWorldData().worldGenSettings().dimensions();
            long seed = world.getServer().getWorldData().worldGenSettings().seed();
            for (int i = 1; i <= ServerConfigs.INSTANCE.maxDimensions.get(); i++) {
                ResourceKey<LevelStem> key = DimensionHelper.getRegistryKey(Registry.LEVEL_STEM_REGISTRY, i);
                if (!registry.containsKey(key)) {
                    registry.register(key, DimensionHelper.createDimension(world.getServer(), seed, i), Lifecycle.experimental());
                }
            }
        }
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
