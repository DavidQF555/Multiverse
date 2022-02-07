package io.github.davidqf555.minecraft.multiverse.common.items;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;

public class CrystalItemTier implements IItemTier {

    public static final CrystalItemTier INSTANCE = new CrystalItemTier();

    protected CrystalItemTier() {
    }

    @Override
    public int getUses() {
        return 250;
    }

    @Override
    public float getSpeed() {
        return 4;
    }

    @Override
    public float getAttackDamageBonus() {
        return 2;
    }

    @Override
    public int getLevel() {
        return 2;
    }

    @Override
    public int getEnchantmentValue() {
        return 22;
    }

    @Nonnull
    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Tags.Items.GLASS);
    }
}
