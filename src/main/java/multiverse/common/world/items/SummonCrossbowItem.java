package multiverse.common.world.items;

import multiverse.common.Multiverse;
import multiverse.common.ServerConfigs;
import multiverse.common.world.ArrowSummonsData;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SummonCrossbowItem extends CrossbowItem {

    private static final Component LORE = new TranslatableComponent(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "beacon_crossbow.lore"))).withStyle(ChatFormatting.GOLD);
    private final TagKey<Item> repair;

    public SummonCrossbowItem(TagKey<Item> repair, Properties properties) {
        super(properties);
        this.repair = repair;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(LORE);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack bow = player.getItemInHand(hand);
        if (world instanceof ServerLevel && isCharged(bow)) {
            ArrowSummonsData.getOrCreate((ServerLevel) world).add(player.getEyePosition(), player.getLookAngle(), player.getUUID(), ServerConfigs.INSTANCE.spawnCount.get(), containsChargedProjectile(bow, Items.FIREWORK_ROCKET));
        }
        return super.use(world, player, hand);
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.is(repair) || super.isValidRepairItem(pToRepair, pRepair);
    }

}
