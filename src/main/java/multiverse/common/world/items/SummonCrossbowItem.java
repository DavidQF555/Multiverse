package multiverse.common.world.items;

import multiverse.common.ServerConfigs;
import multiverse.common.world.ArrowSummonsData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SummonCrossbowItem extends CrossbowItem {

    private final Component lore;

    public SummonCrossbowItem(Properties properties) {
        super(properties);
        lore = Component.translatable(getDescriptionId() + ".lore").withStyle(ChatFormatting.GOLD);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(lore);
    }

    @Nonnull
    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack bow = player.getItemInHand(hand);
        if (world instanceof ServerLevel && isCharged(bow)) {
            ChargedProjectiles proj = bow.get(DataComponents.CHARGED_PROJECTILES);
            ArrowSummonsData.getOrCreate((ServerLevel) world).add(player.getEyePosition(), player.getLookAngle(), player.getUUID(), ServerConfigs.INSTANCE.spawnCount.get(), proj != null && !proj.isEmpty());
        }
        return super.use(world, player, hand);
    }

}
