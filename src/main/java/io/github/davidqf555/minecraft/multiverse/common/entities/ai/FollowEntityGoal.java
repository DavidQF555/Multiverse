package io.github.davidqf555.minecraft.multiverse.common.entities.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.function.Function;

public class FollowEntityGoal<T extends Mob> extends Goal {

    private final T mob;
    private final double start, stop;
    private final float speed;
    private final Function<T, Entity> targetMap;
    private int recalculate;
    private Entity target;

    public FollowEntityGoal(T mob, Function<T, Entity> targetMap, double start, double stop, float speed) {
        this.mob = mob;
        this.targetMap = targetMap;
        this.speed = speed;
        this.start = start;
        this.stop = stop;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        Entity target = targetMap.apply(mob);
        if (target == null || target.isSpectator() || mob.distanceToSqr(target) < start * start) {
            return false;
        }
        this.target = target;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (mob.getNavigation().isDone()) {
            return false;
        }
        return mob.distanceToSqr(target) > stop * stop;
    }

    @Override
    public void start() {
        recalculate = 0;
    }

    @Override
    public void stop() {
        target = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        mob.getLookControl().setLookAt(target, 10, mob.getMaxHeadXRot());
        if (--recalculate <= 0) {
            this.recalculate = 10;
            if (!mob.isPassenger()) {
                mob.getNavigation().moveTo(target, speed);
            }
        }
    }

}

