package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.RiftFeature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class DimensionSlasherItem extends SwordItem {

    public DimensionSlasherItem(int attack, float speed) {
        super(ItemTier.IRON, attack, speed, new Properties().rarity(Rarity.EPIC).tab(ItemGroup.TAB_COMBAT));
    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int remaining) {
        if (world instanceof ServerWorld && entity instanceof PlayerEntity) {
            CooldownTracker cooldowns = ((PlayerEntity) entity).getCooldowns();
            if (!cooldowns.isOnCooldown(this)) {
                int count = Math.min(600, getUseDuration(stack) - remaining);
                int width = 6 + count / 30;
                int height = 3 + count / 40;
                Vector3d look = entity.getLookAngle();
                BlockPos center = new BlockPos(entity.getEyePosition(1).add(look.scale(width + 1.5)));
                RiftFeature.INSTANCE.place((ServerWorld) world, ((ServerWorld) world).getChunkSource().getGenerator(), entity.getRandom(), center, RiftConfig.fixed(Optional.empty(), width, height, 0, 90 - entity.getYHeadRot(), -entity.getViewXRot(1), true, false));
                cooldowns.addCooldown(this, ServerConfigs.INSTANCE.dimensionSlasherCooldown.get());
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.startUsingItem(hand);
        return ActionResult.consume(player.getItemInHand(hand));
    }

}
