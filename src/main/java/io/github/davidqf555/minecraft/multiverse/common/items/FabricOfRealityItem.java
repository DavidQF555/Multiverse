package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.RegistryHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FabricOfRealityItem extends Item {

    private static final Component LORE = new TranslatableComponent("item." + Multiverse.MOD_ID + ".fabric_of_reality.lore").withStyle(ChatFormatting.BLUE);

    public FabricOfRealityItem() {
        super(new Item.Properties()
                .tab(CreativeModeTab.TAB_MATERIALS)
                .rarity(Rarity.UNCOMMON)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(LORE);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos target = context.getClickedPos();
        Level world = context.getLevel();
        if (world instanceof ServerLevel && world.getBlockState(target).getBlock().equals(RegistryHandler.RIFT_BLOCK.get()) && world.destroyBlock(target, true, player)) {
            context.getItemInHand().shrink(1);
            world.playSound(player, target, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1, 1);
            return InteractionResult.CONSUME;
        }
        return super.useOn(context);
    }
}
