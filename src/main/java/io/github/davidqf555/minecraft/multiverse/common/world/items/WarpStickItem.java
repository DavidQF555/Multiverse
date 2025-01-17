package io.github.davidqf555.minecraft.multiverse.common.world.items;

import io.github.davidqf555.minecraft.multiverse.common.world.WarpTeleporter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class WarpStickItem extends Item {

    public WarpStickItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(MultiversalToolHelper.INSTRUCTIONS);
        text.add(MultiversalToolHelper.CROUCH_INSTRUCTIONS);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (!pTarget.level.isClientSide() && !pStack.isEmpty()) {
            WarpTeleporter.warp(pTarget, MultiversalToolHelper.getTarget(pStack));
        }
        return false;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            if (!MultiversalToolHelper.setCurrent(world, stack)) {
                return InteractionResultHolder.pass(stack);
            }
        } else if (world instanceof ServerLevel) {
            MultiversalToolHelper.setRandomTarget(world, stack);
        }
        return InteractionResultHolder.consume(stack);
    }

}
