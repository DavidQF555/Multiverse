package io.github.davidqf555.minecraft.multiverse.common.world.entities.ai;

import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.phys.Vec3;

public class FlyingMoveThroughVillageGoal extends Raider.RaiderMoveThroughVillageGoal {

    public FlyingMoveThroughVillageGoal(Raider pRaider, double pSpeedModifier, int pDistanceToPoi) {
        super(pRaider, pSpeedModifier, pDistanceToPoi);
    }

    @Override
    public void tick() {
        if (raider.getNavigation().isDone()) {
            Vec3 target = Vec3.atBottomCenterOf(poiPos);
            int pY = 0;
            if (target.y() - raider.getY() > 2) {
                pY = 5;
            } else if (target.y() - raider.getY() < -2) {
                pY = -5;
            }
            Vec3 pos = AirRandomPos.getPosTowards(raider, 16, 7, pY, target, Math.PI / 10);
            if (pos == null) {
                pos = AirRandomPos.getPosTowards(raider, 8, 7, pY, target, Math.PI / 2);
            }
            if (pos == null) {
                stuck = true;
                return;
            }
            raider.getNavigation().moveTo(pos.x(), pos.y(), pos.z(), speedModifier);
        }
    }

}
