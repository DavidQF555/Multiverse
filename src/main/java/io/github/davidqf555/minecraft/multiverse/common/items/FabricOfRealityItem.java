package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FabricOfRealityItem extends Item {

    private static final ITextComponent LORE = new TranslationTextComponent("item." + Multiverse.MOD_ID + ".fabric_of_reality.lore").withStyle(TextFormatting.BLUE);

    public FabricOfRealityItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(LORE);
    }

    @Nonnull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        BlockPos target = context.getClickedPos();
        World world = context.getLevel();
        if (world instanceof ServerWorld && world.getBlockState(target).getBlock().equals(BlockRegistry.RIFT.get()) && world.destroyBlock(target, true, player)) {
            context.getItemInHand().shrink(1);
            world.playSound(player, target, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1, 1);
            return ActionResultType.CONSUME;
        }
        return super.useOn(context);
    }
}
