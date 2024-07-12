package io.github.davidqf555.minecraft.multiverse.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SimpleLoreItem extends Item {

    private final boolean foiled;
    private final ChatFormatting formatting;
    private Component lore;

    public SimpleLoreItem(boolean foiled, ChatFormatting formatting, Properties properties) {
        super(properties);
        this.foiled = foiled;
        this.formatting = formatting;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        text.add(getLore());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return foiled;
    }

    public Component getLore() {
        if (lore == null) {
            lore = Component.translatable(getDescriptionId() + ".lore").withStyle(formatting);
        }
        return lore;
    }

}
