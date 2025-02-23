package io.github.davidqf555.minecraft.multiverse.common.world.items;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.WarpTeleporter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class WarpToolItem extends Item {

    private final Component use;

    public WarpToolItem(Properties pProperties) {
        super(pProperties);
        use = Component.literal(" ").append(Component.translatable(getDescriptionId() + ".use").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        text.add(Component.empty());
        text.add(MultiversalToolHelper.getRightHeader());
        text.add(use);
        text.add(MultiversalToolHelper.getShiftRightHeader());
        text.add(MultiversalToolHelper.SELECT_RANDOM);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!MultiversalToolHelper.setCurrent(world, stack)) {
                return InteractionResult.PASS;
            }
        } else if (world.dimension().equals(MultiversalToolHelper.getTarget(stack))) {
            return InteractionResult.PASS;
        } else if (world instanceof ServerLevel) {
            ResourceKey<Level> current = world.dimension();
            ResourceKey<Level> target = MultiversalToolHelper.getTarget(stack);
            Entity copy = WarpTeleporter.warp(player, target);
            if (copy == null) {
                return InteractionResult.PASS;
            }
            MultiversalToolHelper.setTarget(stack, current);
            if (!player.isCreative()) {
                player.getCooldowns().addCooldown(stack, ServerConfigs.INSTANCE.warpRingCooldown.get());
            }
        }
        return InteractionResult.CONSUME;
    }

}
