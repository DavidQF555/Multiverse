package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public final class ArmorMaterialRegistry {

    public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, Multiverse.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> KALEIDITE = register("kaleidite", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.BODY, 11);
                    }
            ), 30, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(ItemRegistry.KALEIDITE_SHARD::get), List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "kaleidite"))), 2, 0));

    private ArmorMaterialRegistry() {
    }

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(String name, Supplier<ArmorMaterial> material) {
        return MATERIALS.register(name, material);
    }

}
