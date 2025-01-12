package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.util.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.util.WarpTeleporter;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class RiftDeathItem extends Item implements IDeathEffect {

    private final int amp;

    public RiftDeathItem(Properties properties, int amp) {
        super(properties);
        this.amp = amp;
    }

    @Override
    public boolean onDeath(LivingEntity entity, ItemStack stack) {
        if (!entity.level.isClientSide()) {
            entity.setHealth(2);
            ResourceKey<Level> current = entity.level.dimension();
            ResourceKey<Level> target = DimensionHelper.randomMultiverseDimension(entity.getRandom(), Optional.of(current));
            Entity copy = WarpTeleporter.warp(entity, target);
            if (copy instanceof LivingEntity) {
                ((LivingEntity) copy).addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, amp - 1));
            }
            if (entity instanceof ServerPlayer) {
                CriteriaTriggers.USED_TOTEM.trigger((ServerPlayer) entity, stack);
            }

        }
        return true;
    }

}
