package io.github.davidqf555.minecraft.multiverse.common.entities.ai;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

//based on PathfindToRaidGoal
public class FlyingPathfindToRaidGoal extends Goal {

    private final Raider mob;
    private final double speed;

    public FlyingPathfindToRaidGoal(Raider mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() == null && !mob.isVehicle() && mob.hasActiveRaid() && !mob.getCurrentRaid().isOver() && !((ServerLevel) mob.level).isVillage(mob.blockPosition());
    }

    @Override
    public boolean canContinueToUse() {
        return mob.hasActiveRaid() && !mob.getCurrentRaid().isOver() && mob.level instanceof ServerLevel && !((ServerLevel) mob.level).isVillage(mob.blockPosition());
    }

    @Override
    public void tick() {
        if (mob.hasActiveRaid()) {
            Raid raid = mob.getCurrentRaid();
            if (!mob.isPathFinding()) {
                Vec3 target = Vec3.atBottomCenterOf(raid.getCenter());
                int pY = 0;
                if (target.y() - mob.getY() > 2) {
                    pY = 3;
                } else if (target.y() - mob.getY() < -2) {
                    pY = -3;
                }
                Vec3 pos = AirRandomPos.getPosTowards(mob, 15, 4, pY, target, Math.PI / 2);
                if (pos != null) {
                    mob.getNavigation().moveTo(pos.x(), pos.y(), pos.z(), speed);
                }
            }
        }

    }

}
