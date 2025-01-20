package io.github.davidqf555.minecraft.multiverse.common.world.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class MultiversalAxeItem extends AxeItem {

    public MultiversalAxeItem(ToolMaterial tier, float damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        text.add(MultiversalToolHelper.LORE);
        text.add(MultiversalToolHelper.INSTRUCTIONS);
        text.add(MultiversalToolHelper.CROUCH_INSTRUCTIONS);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (super.mineBlock(stack, world, state, pos, entity)) {
            if (entity instanceof Player && world instanceof ServerLevel) {
                MultiversalToolHelper.mineBlock((Player) entity, (ServerLevel) world, stack, pos);
            }
            return true;
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
