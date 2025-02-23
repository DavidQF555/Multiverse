package multiverse.common.world.items;

import multiverse.common.ServerConfigs;
import multiverse.common.world.RiftHelper;
import multiverse.common.world.WarpTeleporter;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class WarpShieldItem extends SimpleLoreItem {

    public WarpShieldItem(ChatFormatting color, Properties pProperties) {
        super(false, color, pProperties);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack pStack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void onUseTick(Level world, LivingEntity player, ItemStack stack, int count) {
        if (!world.isClientSide()) {
            double range = ServerConfigs.INSTANCE.shieldRange.get();
            AABB bounds = AABB.ofSize(player.getEyePosition(), range * 2, range * 2, range * 2);
            for (Projectile proj : world.getEntitiesOfClass(Projectile.class, bounds)) {
                RiftHelper.randomTargetDimension(player.getRandom(), player.level().dimension()).ifPresent(target -> WarpTeleporter.warp(proj, target));
            }
        }
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        pPlayer.startUsingItem(pHand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
        return ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }

}
