package io.github.davidqf555.minecraft.multiverse.common.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IDeathEffect {

    boolean onDeath(LivingEntity entity, ItemStack stack);

}
