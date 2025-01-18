package io.github.davidqf555.minecraft.multiverse.common.world.items;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.ArrowSummonsData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SummonCrossbowItem extends CrossbowItem {

    private final TagKey<Item> repair;

    public SummonCrossbowItem(TagKey<Item> repair, Properties properties) {
        super(properties);
        this.repair = repair;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack bow = player.getItemInHand(hand);
        if (world instanceof ServerLevel && isCharged(bow)) {
            ChargedProjectiles proj = bow.get(DataComponents.CHARGED_PROJECTILES);
            ArrowSummonsData.getOrCreate((ServerLevel) world).add(player.getEyePosition(), player.getLookAngle(), player.getUUID(), ServerConfigs.INSTANCE.spawnCount.get(), proj != null && !proj.isEmpty());
        }
        return super.use(world, player, hand);
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.is(repair) || super.isValidRepairItem(pToRepair, pRepair);
    }

}
