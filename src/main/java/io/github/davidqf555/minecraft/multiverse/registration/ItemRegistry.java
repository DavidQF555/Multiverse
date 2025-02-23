package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import io.github.davidqf555.minecraft.multiverse.common.world.items.*;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Multiverse.MOD_ID);
    public static final CreativeModeTab TAB = new CreativeModeTab(Multiverse.MOD_ID) {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return KALEIDITE_SHARD.get().getDefaultInstance();
        }
    };

    private ItemRegistry() {
    }

    public static final RegistryObject<RiftCoreItem> KALEIDITE_CORE = register("kaleidite_core", () -> new RiftCoreItem(ChatFormatting.LIGHT_PURPLE, new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> KALEIDITE_SHARD = register("kaleidite_shard", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<SimpleLoreItem> MULTIVERSAL_BEACON = register("multiversal_beacon", () -> new SimpleLoreItem(true, ChatFormatting.GOLD, new Item.Properties().tab(TAB).rarity(Rarity.RARE)));
    public static final RegistryObject<ArmorItem> KALEIDITE_HELMET = register("kaleidite_helmet", () -> new ArmorItem(KaleiditeArmorMaterial.KALEIDITE, EquipmentSlot.HEAD, new Item.Properties().tab(TAB)));
    public static final RegistryObject<ArmorItem> KALEIDITE_CHESTPLATE = register("kaleidite_chestplate", () -> new ArmorItem(KaleiditeArmorMaterial.KALEIDITE, EquipmentSlot.CHEST, new Item.Properties().tab(TAB)));
    public static final RegistryObject<ArmorItem> KALEIDITE_LEGGINGS = register("kaleidite_leggings", () -> new ArmorItem(KaleiditeArmorMaterial.KALEIDITE, EquipmentSlot.LEGS, new Item.Properties().tab(TAB)));
    public static final RegistryObject<ArmorItem> KALEIDITE_BOOTS = register("kaleidite_boots", () -> new ArmorItem(KaleiditeArmorMaterial.KALEIDITE, EquipmentSlot.FEET, new Item.Properties().tab(TAB)));
    public static final RegistryObject<SummonCrossbowItem> BEACON_CROSSBOW = register("beacon_crossbow", () -> new SummonCrossbowItem(MultiverseTags.KALEIDITE_MATERIALS, new Item.Properties().stacksTo(1).durability(465).tab(TAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<WarpShieldItem> WARP_SHIELD = register("warp_shield", () -> new WarpShieldItem(MultiverseTags.KALEIDITE_MATERIALS, ChatFormatting.GOLD, new Item.Properties().durability(336).tab(TAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<SwordItem> KALEIDITE_SWORD = register("kaleidite_sword", () -> new SwordItem(KaleiditeItemTier.INSTANCE, 3, -2.4f, new Item.Properties().tab(TAB)));
    public static final RegistryObject<PickaxeItem> KALEIDITE_PICKAXE = register("kaleidite_pickaxe", () -> new PickaxeItem(KaleiditeItemTier.INSTANCE, 1, -2.8f, new Item.Properties().tab(TAB)));
    public static final RegistryObject<ShovelItem> KALEIDITE_SHOVEL = register("kaleidite_shovel", () -> new ShovelItem(KaleiditeItemTier.INSTANCE, 1.5f, -3, new Item.Properties().tab(TAB)));
    public static final RegistryObject<AxeItem> KALEIDITE_AXE = register("kaleidite_axe", () -> new AxeItem(KaleiditeItemTier.INSTANCE, 6, -3.1f, new Item.Properties().tab(TAB)));
    public static final RegistryObject<RiftSwordItem> PRISMATIC_SWORD = register("prismatic_sword", () -> new RiftSwordItem(KaleiditeItemTier.INSTANCE, 4, -2.4f, new Item.Properties().rarity(Rarity.EPIC).tab(TAB)));
    public static final RegistryObject<MultiversalPickaxeItem> PRISMATIC_PICKAXE = register("prismatic_pickaxe", () -> new MultiversalPickaxeItem(KaleiditeItemTier.INSTANCE, 2, -2.8f, new Item.Properties().rarity(Rarity.EPIC).tab(TAB)));
    public static final RegistryObject<MultiversalShovelItem> PRISMATIC_SHOVEL = register("prismatic_shovel", () -> new MultiversalShovelItem(KaleiditeItemTier.INSTANCE, 2.5f, -3, new Item.Properties().rarity(Rarity.EPIC).tab(TAB)));
    public static final RegistryObject<MultiversalAxeItem> PRISMATIC_AXE = register("prismatic_axe", () -> new MultiversalAxeItem(KaleiditeItemTier.INSTANCE, 6, -2.1f, new Item.Properties().rarity(Rarity.EPIC).tab(TAB)));
    public static final RegistryObject<SimpleLoreItem> DIMENSIONAL_PRISM = register("dimensional_prism", () -> new SimpleLoreItem(true, ChatFormatting.GOLD, new Item.Properties().tab(TAB).rarity(Rarity.RARE)));
    public static final RegistryObject<BeaconArmorItem> BEACON_CHESTPLATE = register("beacon_chestplate", () -> new BeaconArmorItem(KaleiditeArmorMaterial.BEACON, EquipmentSlot.CHEST, new Item.Properties().tab(TAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<WarpToolItem> WARP_RING = register("warp_ring", () -> new WarpToolItem(new Item.Properties().stacksTo(1).tab(TAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<WarpStickItem> WARP_STICK = register("warp_stick", () -> new WarpStickItem(ChatFormatting.GOLD, new Item.Properties().stacksTo(1).tab(TAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<ForgeSpawnEggItem> TRAVELER_SPAWN_EGG = register("traveler_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.TRAVELER, 0x5BE6FF, 0x4A6CF7, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<ForgeSpawnEggItem> CONQUEROR_SPAWN_EGG = register("conqueror_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.CONQUEROR, 0x5BE6FF, 0xE0B230, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistryObject<BlockItem> KALEIDITE_CLUSTER = register("kaleidite_cluster", () -> new BlockItem(BlockRegistry.KALEIDITE_CLUSTER.get(), new Item.Properties().tab(TAB)));

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

}