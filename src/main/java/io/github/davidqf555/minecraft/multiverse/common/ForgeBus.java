package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.common.items.IDeathEffect;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.ShapesManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ForgeBus {

    private ForgeBus() {
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ShapesManager.INSTANCE.load(event.getServer());
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Pre event) {
        Level level = event.getLevel();
        if (!level.isClientSide()) {
            ArrowSummonsData.get((ServerLevel) level).ifPresent(data -> data.tick((ServerLevel) level));
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
