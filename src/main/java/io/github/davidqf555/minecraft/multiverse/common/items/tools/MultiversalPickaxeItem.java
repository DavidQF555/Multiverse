package io.github.davidqf555.minecraft.multiverse.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MultiversalPickaxeItem extends PickaxeItem {

    public MultiversalPickaxeItem(Tier tier, int damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(MultiversalToolHelper.LORE);
        text.add(MultiversalToolHelper.INSTRUCTIONS);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (super.mineBlock(stack, world, state, pos, entity)) {
            if (entity instanceof Player && world instanceof ServerLevel) {
                MultiversalToolHelper.mineBlock(entity, (ServerLevel) world, stack, pos);
            }
            return true;
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (world instanceof ServerLevel) {
            ItemStack stack = player.getItemInHand(hand);
            if (player.isCrouching()) {
                MultiversalToolHelper.setCurrent(world, stack);
            } else {
                MultiversalToolHelper.setRandomTarget((ServerLevel) world, stack);
            }
        }
        return super.use(world, player, hand);
    }

}
