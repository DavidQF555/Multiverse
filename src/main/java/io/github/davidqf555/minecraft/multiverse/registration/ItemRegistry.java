package io.github.davidqf555.minecraft.multiverse.registration;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import io.github.davidqf555.minecraft.multiverse.common.world.items.*;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ItemRegistry {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Multiverse.MOD_ID);
    public static final ArmorMaterial KALEIDITE_ARMOR = new ArmorMaterial(33,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                        map.put(ArmorType.BOOTS, 3);
                        map.put(ArmorType.LEGGINGS, 6);
                        map.put(ArmorType.CHESTPLATE, 8);
                        map.put(ArmorType.HELMET, 3);
                    }
            ), 30, SoundEvents.ARMOR_EQUIP_DIAMOND, 2, 0, MultiverseTags.KALEIDITE_MATERIALS, ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "kaleidite")));
    public static final ArmorMaterial BEACON_ARMOR = new ArmorMaterial(33,
            Util.make(new EnumMap<>(ArmorType.class), map -> map.put(ArmorType.CHESTPLATE, 8)
            ), 30, SoundEvents.ARMOR_EQUIP_DIAMOND, 2, 0, MultiverseTags.KALEIDITE_MATERIALS, ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "beacon")));
    public static final ToolMaterial KALEIDITE_TOOLS = new ToolMaterial(BlockTags.INCORRECT_FOR_STONE_TOOL, 250, 4, 2, 22, MultiverseTags.KALEIDITE_MATERIALS);
    private static final List<Pair<List<ResourceKey<CreativeModeTab>>, DeferredHolder<Item, ? extends Item>>> TABS = new LinkedList<>();
    public static final DeferredItem<RiftCoreItem> KALEIDITE_CORE = register("kaleidite_core", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), RiftCoreItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> KALEIDITE_SHARD = register("kaleidite_shard", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.INGREDIENTS), Item::new, new Item.Properties());
    public static final DeferredItem<SwordItem> KALEIDITE_SWORD = register("kaleidite_sword", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new SwordItem(KALEIDITE_TOOLS, 3, -2.4f, prop), new Item.Properties());
    public static final DeferredItem<PickaxeItem> KALEIDITE_PICKAXE = register("kaleidite_pickaxe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new PickaxeItem(KALEIDITE_TOOLS, 1, -2.8f, prop), new Item.Properties());
    public static final DeferredItem<ShovelItem> KALEIDITE_SHOVEL = register("kaleidite_shovel", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new ShovelItem(KALEIDITE_TOOLS, 1.5f, -3, prop), new Item.Properties());
    public static final DeferredItem<AxeItem> KALEIDITE_AXE = register("kaleidite_axe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new AxeItem(KALEIDITE_TOOLS, 6, -3.1f, prop), new Item.Properties());
    public static final DeferredItem<RiftSwordItem> PRISMATIC_SWORD = register("prismatic_sword", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new RiftSwordItem(KALEIDITE_TOOLS, 4, -2.4f, prop), new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<MultiversalPickaxeItem> PRISMATIC_PICKAXE = register("prismatic_pickaxe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new MultiversalPickaxeItem(KALEIDITE_TOOLS, 2, -2.8f, prop), new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<MultiversalShovelItem> PRISMATIC_SHOVEL = register("prismatic_shovel", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new MultiversalShovelItem(KALEIDITE_TOOLS, 2.5f, -3, prop), new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<MultiversalAxeItem> PRISMATIC_AXE = register("prismatic_axe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new MultiversalAxeItem(KALEIDITE_TOOLS, 6, -2.1f, prop), new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<SimpleLoreItem> MULTIVERSAL_BEACON = register("multiversal_beacon", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), prop -> new SimpleLoreItem(true, ChatFormatting.GOLD, prop), new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredItem<ArmorItem> KALEIDITE_HELMET = register("kaleidite_helmet", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new ArmorItem(KALEIDITE_ARMOR, ArmorType.HELMET, prop), new Item.Properties());
    public static final DeferredItem<ArmorItem> KALEIDITE_CHESTPLATE = register("kaleidite_chestplate", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new ArmorItem(KALEIDITE_ARMOR, ArmorType.CHESTPLATE, prop), new Item.Properties());
    public static final DeferredItem<ArmorItem> KALEIDITE_LEGGINGS = register("kaleidite_leggings", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new ArmorItem(KALEIDITE_ARMOR, ArmorType.LEGGINGS, prop), new Item.Properties());
    public static final DeferredItem<ArmorItem> KALEIDITE_BOOTS = register("kaleidite_boots", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new ArmorItem(KALEIDITE_ARMOR, ArmorType.BOOTS, prop), new Item.Properties());
    public static final DeferredItem<SummonCrossbowItem> BEACON_CROSSBOW = register("beacon_crossbow", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), SummonCrossbowItem::new, () -> new Item.Properties().repairable(MultiverseTags.KALEIDITE_MATERIALS).stacksTo(1).durability(465).component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).enchantable(1).rarity(Rarity.EPIC));
    public static final DeferredItem<SimpleLoreItem> DIMENSIONAL_PRISM = register("dimensional_prism", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), prop -> new SimpleLoreItem(true, ChatFormatting.GOLD, prop), new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredItem<BeaconArmorItem> BEACON_CHESTPLATE = register("beacon_chestplate", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new BeaconArmorItem(BEACON_ARMOR, ArmorType.CHESTPLATE, prop), new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<WarpShieldItem> WARP_SHIELD = register("warp_shield", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), WarpShieldItem::new, () -> new Item.Properties().durability(336).repairable(MultiverseTags.KALEIDITE_MATERIALS).equippableUnswappable(EquipmentSlot.OFFHAND).rarity(Rarity.EPIC));
    public static final DeferredItem<WarpToolItem> WARP_RING = register("warp_ring", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), WarpToolItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    public static final DeferredItem<WarpStickItem> WARP_STICK = register("warp_stick", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), WarpStickItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredItem<BlockItem> KALEIDITE_CLUSTER = register("kaleidite_cluster", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.NATURAL_BLOCKS), prop -> new BlockItem(BlockRegistry.KALEIDITE_CLUSTER.get(), prop), new Item.Properties());

    private static <T extends Item> DeferredItem<T> register(String name, List<ResourceKey<CreativeModeTab>> tab, Function<Item.Properties, T> item, Item.Properties base) {
        DeferredItem<T> out = ITEMS.registerItem(name, item, base);
        TABS.add(Pair.of(tab, out));
        return out;
    }

    private static <T extends Item> DeferredItem<T> register(String name, List<ResourceKey<CreativeModeTab>> tab, Function<Item.Properties, T> item, Supplier<Item.Properties> base) {
        DeferredItem<T> out = ITEMS.register(name, key -> item.apply(base.get().setId(ResourceKey.create(Registries.ITEM, key))));
        TABS.add(Pair.of(tab, out));
        return out;
    }

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        TABS.stream().filter(pair -> pair.getFirst().contains(event.getTabKey())).map(Pair::getSecond).forEach(item -> event.accept(item::get));
    }

}
