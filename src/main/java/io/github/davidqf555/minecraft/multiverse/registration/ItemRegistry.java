package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.items.*;
import io.github.davidqf555.minecraft.multiverse.common.items.tools.MultiversalAxeItem;
import io.github.davidqf555.minecraft.multiverse.common.items.tools.MultiversalPickaxeItem;
import io.github.davidqf555.minecraft.multiverse.common.items.tools.MultiversalShovelItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Multiverse.MOD_ID);
    public static final CreativeModeTab TAB = new CreativeModeTab(Multiverse.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return KALEIDITE_SHARD.get().getDefaultInstance();
        }
    };

    public static final RegistryObject<RiftSwordItem> BOUNDLESS_BLADE = register("boundless_blade", () -> new RiftSwordItem(KaleiditeItemTier.INSTANCE, 2, -2.4f, new Item.Properties().rarity(Rarity.EPIC).tab(TAB)));
    public static final RegistryObject<SpawnCollectorItem> UNIVERSAL_TREASURE = register("universal_treasure", () -> new SpawnCollectorItem(new Item.Properties().rarity(Rarity.RARE).tab(TAB).stacksTo(1).fireResistant(), 300));
    public static final RegistryObject<RiftDeathItem> TOTEM_OF_ESCAPE = register("totem_of_escape", () -> new RiftDeathItem(new Item.Properties().stacksTo(1).tab(TAB).rarity(Rarity.UNCOMMON), 5));
    public static final RegistryObject<RiftCoreItem> KALEIDITE_CORE = register("kaleidite_core", () -> new RiftCoreItem(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> KALEIDITE_SHARD = register("kaleidite_shard", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<SimpleFoiledItem> MULTIVERSAL_BEACON = register("multiversal_beacon", () -> new SimpleFoiledItem(new Item.Properties().tab(TAB).rarity(Rarity.RARE)));
    public static final RegistryObject<ArmorItem> KALEIDITE_HELMET = register("kaleidite_helmet", () -> new ArmorItem(KaleiditeArmorMaterial.INSTANCE, EquipmentSlot.HEAD, new Item.Properties().tab(TAB)));
    public static final RegistryObject<BeaconArmorItem> KALEIDITE_CHESTPLATE = register("kaleidite_chestplate", () -> new BeaconArmorItem(KaleiditeArmorMaterial.INSTANCE, EquipmentSlot.CHEST, new Item.Properties().tab(TAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<ArmorItem> KALEIDITE_LEGGINGS = register("kaleidite_leggings", () -> new ArmorItem(KaleiditeArmorMaterial.INSTANCE, EquipmentSlot.LEGS, new Item.Properties().tab(TAB)));
    public static final RegistryObject<ArmorItem> KALEIDITE_BOOTS = register("kaleidite_boots", () -> new ArmorItem(KaleiditeArmorMaterial.INSTANCE, EquipmentSlot.FEET, new Item.Properties().tab(TAB)));
    public static final RegistryObject<SummonCrossbowItem> KALEIDITE_CROSSBOW = register("kaleidite_crossbow", () -> new SummonCrossbowItem(new Item.Properties().tab(TAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<MultiversalPickaxeItem> KALEIDITE_PICKAXE = register("kaleidite_pickaxe", () -> new MultiversalPickaxeItem(KaleiditeItemTier.INSTANCE, 1, -2.8f, new Item.Properties().tab(TAB)));
    public static final RegistryObject<MultiversalShovelItem> KALEIDITE_SHOVEL = register("kaleidite_shovel", () -> new MultiversalShovelItem(KaleiditeItemTier.INSTANCE, 1, -2.8f, new Item.Properties().tab(TAB)));
    public static final RegistryObject<MultiversalAxeItem> KALEIDITE_AXE = register("kaleidite_axe", () -> new MultiversalAxeItem(KaleiditeItemTier.INSTANCE, 1, -2.8f, new Item.Properties().tab(TAB)));

    public static final RegistryObject<BlockItem> KALEIDITE_CLUSTER = register("kaleidite_cluster", () -> new BlockItem(BlockRegistry.KALEIDITE_CLUSTER.get(), new Item.Properties().tab(TAB)));

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

    private ItemRegistry() {
    }

}
