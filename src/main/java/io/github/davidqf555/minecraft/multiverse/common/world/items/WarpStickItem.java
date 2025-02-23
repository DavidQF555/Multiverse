package io.github.davidqf555.minecraft.multiverse.common.world.items;

import io.github.davidqf555.minecraft.multiverse.common.world.WarpTeleporter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

public class WarpStickItem extends SimpleLoreItem {

    public WarpStickItem(ChatFormatting format, Properties pProperties) {
        super(false, format, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        text.add(Component.empty());
        text.add(MultiversalToolHelper.getRightHeader());
        text.add(MultiversalToolHelper.SELECT_RANDOM);
        text.add(MultiversalToolHelper.getShiftRightHeader());
        text.add(MultiversalToolHelper.SELECT_CURRENT);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (!pTarget.level().isClientSide() && !pStack.isEmpty()) {
            WarpTeleporter.warp(pTarget, MultiversalToolHelper.getTarget(pStack));
        }
        return false;
    }

    @Nonnull
    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            if (!MultiversalToolHelper.setCurrent(world, stack)) {
                return InteractionResult.PASS;
            }
        } else if (world instanceof ServerLevel) {
            MultiversalToolHelper.setRandomTarget(world, stack);
        }
        return InteractionResult.CONSUME;
    }

}
