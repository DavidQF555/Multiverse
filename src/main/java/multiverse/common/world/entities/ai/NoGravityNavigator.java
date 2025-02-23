package multiverse.common.world.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;

public class NoGravityNavigator extends FlyingPathNavigation {

    public NoGravityNavigator(Mob pMob, Level pLevel) {
        super(pMob, pLevel);
    }

    @Override
    public boolean isStableDestination(BlockPos pPos) {
        return true;
    }

}
