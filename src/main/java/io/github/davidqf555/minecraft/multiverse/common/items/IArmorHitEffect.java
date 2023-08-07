package io.github.davidqf555.minecraft.multiverse.common.items;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface IArmorHitEffect {

    boolean onHit(LivingEntity entity, DamageSource source, float damage);

}
