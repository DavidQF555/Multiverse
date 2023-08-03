package io.github.davidqf555.minecraft.multiverse.common.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;
import java.util.function.Function;

public class EntityHurtTargetGoal<T extends Mob> extends TargetGoal {

    private final T mob;
    private final Function<T, LivingEntity> target;
    private LivingEntity lastHurt;
    private int timestamp;

    public EntityHurtTargetGoal(T mob, Function<T, LivingEntity> target) {
        super(mob, false);
        this.mob = mob;
        this.target = target;
        setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.target.apply(mob);
        if (target == null) {
            return false;
        } else {
            lastHurt = target.getLastHurtMob();
            int i = target.getLastHurtMobTimestamp();
            return i != timestamp && canAttack(lastHurt, TargetingConditions.DEFAULT);
        }
    }

    @Override
    public void start() {
        mob.setTarget(lastHurt);
        LivingEntity target = this.target.apply(mob);
        if (target != null) {
            timestamp = target.getLastHurtMobTimestamp();
        }
        super.start();
    }

}
