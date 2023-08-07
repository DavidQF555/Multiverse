package io.github.davidqf555.minecraft.multiverse.common.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;
import java.util.function.Function;

public class EntityHurtByTargetGoal<T extends Mob> extends TargetGoal {

    private final T mob;
    private final Function<T, LivingEntity> entityMap;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public EntityHurtByTargetGoal(T mob, Function<T, LivingEntity> entityMap) {
        super(mob, false);
        this.mob = mob;
        this.entityMap = entityMap;
        setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity entity = entityMap.apply(mob);
        if (entity == null) {
            return false;
        } else {
            ownerLastHurtBy = entity.getLastHurtByMob();
            int i = entity.getLastHurtByMobTimestamp();
            return i != timestamp && canAttack(ownerLastHurtBy, TargetingConditions.DEFAULT);
        }
    }

    @Override
    public void start() {
        mob.setTarget(ownerLastHurtBy);
        LivingEntity entity = entityMap.apply(mob);
        if (entity != null) {
            timestamp = entity.getLastHurtByMobTimestamp();
        }
        super.start();
    }

}
