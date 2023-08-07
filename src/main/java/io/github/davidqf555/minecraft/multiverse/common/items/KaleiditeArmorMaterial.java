package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.registration.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class KaleiditeArmorMaterial implements ArmorMaterial {

    public static final KaleiditeArmorMaterial INSTANCE = new KaleiditeArmorMaterial();
    private final String name = new ResourceLocation(Multiverse.MOD_ID, "kaleidite").toString();
    private final int[] defense = new int[]{3, 6, 8, 3};
    private final int[] durability = new int[]{13, 15, 16, 11};

    protected KaleiditeArmorMaterial() {
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type slot) {
        return durability[slot.getSlot().getIndex()] * 33;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type slot) {
        return defense[slot.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return 30;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_DIAMOND;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ItemRegistry.KALEIDITE_SHARD.get());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return 2;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }

}
