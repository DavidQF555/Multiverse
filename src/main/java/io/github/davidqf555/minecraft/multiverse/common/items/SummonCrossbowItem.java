package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.ArrowSummonsData;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SummonCrossbowItem extends CrossbowItem {

    public SummonCrossbowItem(Properties properties) {
        super(properties.stacksTo(1));
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

}
