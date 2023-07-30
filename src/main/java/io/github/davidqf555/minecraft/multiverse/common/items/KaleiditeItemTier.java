package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.registration.ItemRegistry;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;

public class KaleiditeItemTier implements Tier {

    public static final KaleiditeItemTier INSTANCE = new KaleiditeItemTier();

    protected KaleiditeItemTier() {
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
        return Ingredient.of(ItemRegistry.KALEIDITE.get());
    }
}
