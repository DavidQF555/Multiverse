package io.github.davidqf555.minecraft.multiverse.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
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
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
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
