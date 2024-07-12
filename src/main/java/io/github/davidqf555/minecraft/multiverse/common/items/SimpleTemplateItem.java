package io.github.davidqf555.minecraft.multiverse.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class SimpleTemplateItem extends SmithingTemplateItem {

    private final boolean foiled;

    public SimpleTemplateItem(boolean foiled, Component appliesTo, Component ingredients, Component upgradeDescription, Component baseSlotDescription, Component additionsSlotDescription, List<ResourceLocation> baseSlotEmptyIcons, List<ResourceLocation> additionalSlotEmptyIcons) {
        super(appliesTo, ingredients, upgradeDescription, baseSlotDescription, additionsSlotDescription, baseSlotEmptyIcons, additionalSlotEmptyIcons);
        this.foiled = foiled;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return foiled;
    }

}
