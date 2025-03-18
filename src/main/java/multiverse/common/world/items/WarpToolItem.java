package multiverse.common.world.items;

import multiverse.common.Multiverse;
import multiverse.common.ServerConfigs;
import multiverse.common.world.WarpTeleporter;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarpToolItem extends Item {

    private static final Component USE = new TextComponent(" ").append(new TranslatableComponent(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "warp_ring.use"))).withStyle(ChatFormatting.AQUA));

    public WarpToolItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(TextComponent.EMPTY);
        text.add(MultiversalToolHelper.getRightHeader());
        text.add(USE);
        text.add(MultiversalToolHelper.getShiftRightHeader());
        text.add(MultiversalToolHelper.SELECT_RANDOM);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!MultiversalToolHelper.setCurrent(world, stack)) {
                return InteractionResultHolder.pass(stack);
            }
        } else {
            ResourceKey<Level> target = MultiversalToolHelper.getTarget(stack);
            if (target == null || target.equals(world.dimension())) {
                return InteractionResultHolder.pass(stack);
            } else if (world instanceof ServerLevel) {
                ResourceKey<Level> current = world.dimension();
                Entity copy = WarpTeleporter.warp(player, target);
                if (copy == null) {
                    return InteractionResultHolder.pass(stack);
                }
                MultiversalToolHelper.setTarget(stack, current);
                if (!player.isCreative()) {
                    player.getCooldowns().addCooldown(this, ServerConfigs.INSTANCE.warpRingCooldown.get());
                }
            }
        }
        return InteractionResultHolder.consume(stack);
    }

}
