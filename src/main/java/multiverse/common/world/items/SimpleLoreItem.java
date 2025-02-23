package multiverse.common.world.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SimpleLoreItem extends Item {

    private final boolean foiled;
    private final Component lore;

    public SimpleLoreItem(boolean foiled, ChatFormatting formatting, Properties properties) {
        super(properties);
        this.foiled = foiled;
        lore = Component.translatable(getDescriptionId() + ".lore").withStyle(formatting);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        text.add(lore);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return foiled || super.isFoil(stack);
    }

}
