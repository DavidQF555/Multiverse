package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

@MethodsReturnNonnullByDefault
public class KaleiditeArmorMaterial implements ArmorMaterial {

    public static final KaleiditeArmorMaterial KALEIDITE = new KaleiditeArmorMaterial("kaleidite");
    public static final KaleiditeArmorMaterial BEACON = new KaleiditeArmorMaterial("beacon");
    private static final int[] DEFENSE = new int[]{3, 6, 8, 3};
    private static final int[] DURABILITY = new int[]{13, 15, 16, 11};
    private final String name;

    protected KaleiditeArmorMaterial(String name) {
        this.name = new ResourceLocation(Multiverse.MOD_ID, name).toString();
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot) {
        return DURABILITY[slot.getIndex()] * 33;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot) {
        return DEFENSE[slot.getIndex()];
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
        return Ingredient.of(MultiverseTags.KALEIDITE_MATERIALS);
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
