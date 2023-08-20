package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.data.ArrowSummonsData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SummonCrossbowItem extends CrossbowItem {

    private final int count;

    public SummonCrossbowItem(Properties properties, int count) {
        super(properties);
        this.count = count;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack bow = player.getItemInHand(hand);
        if (world instanceof ServerLevel && isCharged(bow)) {
            ArrowSummonsData.getOrCreate((ServerLevel) world).add(player.getEyePosition(), player.getLookAngle(), player.getUUID(), count, containsChargedProjectile(bow, Items.FIREWORK_ROCKET));
        }
        return super.use(world, player, hand);
    }

}
