package io.github.davidqf555.minecraft.multiverse.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class SimpleTemplateItem extends SmithingTemplateItem {

    private final Rarity rarity;
    private final boolean foiled;

    public SimpleTemplateItem(boolean foiled, Rarity rarity, Component appliesTo, Component ingredients, Component upgradeDescription, Component baseSlotDescription, Component additionsSlotDescription, List<ResourceLocation> baseSlotEmptyIcons, List<ResourceLocation> additionalSlotEmptyIcons) {
        super(appliesTo, ingredients, upgradeDescription, baseSlotDescription, additionsSlotDescription, baseSlotEmptyIcons, additionalSlotEmptyIcons);
        this.rarity = rarity;
        this.foiled = foiled;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return foiled;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        if (!stack.isEnchanted()) {
            return rarity;
        }
        return super.getRarity(stack);
    }

    @Override
    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

}
