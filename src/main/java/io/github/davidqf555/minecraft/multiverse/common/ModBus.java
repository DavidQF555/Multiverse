package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.common.entities.KaleiditeCoreEntity;
import io.github.davidqf555.minecraft.multiverse.registration.ItemRegistry;
import net.minecraft.Util;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBus {

    private ModBus() {
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> DispenserBlock.registerBehavior(ItemRegistry.KALEIDITE_CORE.get(), new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level world, Position pos, ItemStack stack) {
                return Util.make(new KaleiditeCoreEntity(pos.x(), pos.y(), pos.z(), world), entity -> entity.setItem(stack));
            }
        }));
    }

}
