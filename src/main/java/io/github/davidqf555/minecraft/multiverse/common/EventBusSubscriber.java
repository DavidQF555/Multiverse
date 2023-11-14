package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.common.data.ArrowSummonsData;
import io.github.davidqf555.minecraft.multiverse.common.items.IDeathEffect;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.IMultiverseNoiseGeneratorSettings;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.BiomesManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.dimension_types.effects.EffectsManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.dimension_types.time.TimesManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.SeaLevelSelectorManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.shapes.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.shapes.ShapesManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EventBusSubscriber {

    private EventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        Registry<NoiseGeneratorSettings> settings = event.getServer().registryAccess().registryOrThrow(Registries.NOISE_SETTINGS);
        for (MultiverseShape shape : MultiverseShape.values()) {
            for (MultiverseType type : MultiverseType.values()) {
                ((IMultiverseNoiseGeneratorSettings) (Object) settings.get(shape.getNoiseSettingsKey(type))).setSettings(shape, type);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ShapesManager.INSTANCE.load(event.getServer());
        BiomesManager.INSTANCE.load(event.getServer());
        TimesManager.INSTANCE.load(event.getServer());
        EffectsManager.INSTANCE.load(event.getServer());
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(SeaLevelSelectorManager.INSTANCE);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.level.isClientSide()) {
            ArrowSummonsData.get((ServerLevel) event.level).ifPresent(data -> data.tick((ServerLevel) event.level));
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
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
