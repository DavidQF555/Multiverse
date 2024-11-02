package io.github.davidqf555.minecraft.multiverse.registration;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import io.github.davidqf555.minecraft.multiverse.common.items.*;
import io.github.davidqf555.minecraft.multiverse.common.items.tools.MultiversalAxeItem;
import io.github.davidqf555.minecraft.multiverse.common.items.tools.MultiversalPickaxeItem;
import io.github.davidqf555.minecraft.multiverse.common.items.tools.MultiversalShovelItem;
import io.github.davidqf555.minecraft.multiverse.common.items.tools.RiftSwordItem;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Multiverse.MOD_ID);
    private static final List<Pair<List<ResourceKey<CreativeModeTab>>, DeferredHolder<Item, ? extends Item>>> TABS = new LinkedList<>();
    public static final ArmorMaterial KALEIDITE_ARMOR = new ArmorMaterial(33,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                        map.put(ArmorType.BOOTS, 3);
                        map.put(ArmorType.LEGGINGS, 6);
                        map.put(ArmorType.CHESTPLATE, 8);
                        map.put(ArmorType.HELMET, 3);
                        map.put(ArmorType.BODY, 11);
                    }
            ), 30, SoundEvents.ARMOR_EQUIP_DIAMOND, 2, 0, MultiverseTags.KALEIDITE_TOOL_MATERIALS, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "kaleidite"));
    public static final ToolMaterial KALEIDITE_TOOLS = new ToolMaterial(BlockTags.INCORRECT_FOR_STONE_TOOL, 250, 4, 2, 22, MultiverseTags.KALEIDITE_TOOL_MATERIALS);

    public static final DeferredHolder<Item, SpawnCollectorItem> UNIVERSAL_TREASURE = register("universal_treasure", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), prop -> new SpawnCollectorItem(prop, 300), new Item.Properties().rarity(Rarity.RARE).stacksTo(1).fireResistant());
    public static final DeferredHolder<Item, RiftDeathItem> TOTEM_OF_ESCAPE = register("totem_of_escape", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), prop -> new RiftDeathItem(prop, 5), new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final DeferredHolder<Item, RiftCoreItem> KALEIDITE_CORE = register("kaleidite_core", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), RiftCoreItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final DeferredHolder<Item, Item> KALEIDITE_SHARD = register("kaleidite_shard", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.INGREDIENTS), Item::new, new Item.Properties());
    public static final DeferredHolder<Item, SwordItem> KALEIDITE_SWORD = register("kaleidite_sword", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new SwordItem(KALEIDITE_TOOLS, 3, -2.4f, prop), new Item.Properties());
    public static final DeferredHolder<Item, PickaxeItem> KALEIDITE_PICKAXE = register("kaleidite_pickaxe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new PickaxeItem(KALEIDITE_TOOLS, 1, -2.8f, prop), new Item.Properties());
    public static final DeferredHolder<Item, ShovelItem> KALEIDITE_SHOVEL = register("kaleidite_shovel", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new ShovelItem(KALEIDITE_TOOLS, 1.5f, -3, prop), new Item.Properties());
    public static final DeferredHolder<Item, AxeItem> KALEIDITE_AXE = register("kaleidite_axe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new AxeItem(KALEIDITE_TOOLS, 6, -3.1f, prop), new Item.Properties());
    public static final DeferredHolder<Item, RiftSwordItem> PRISMATIC_SWORD = register("prismatic_sword", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new RiftSwordItem(KALEIDITE_TOOLS, 4, -2.4f, prop), new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredHolder<Item, MultiversalPickaxeItem> PRISMATIC_PICKAXE = register("prismatic_pickaxe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new MultiversalPickaxeItem(KALEIDITE_TOOLS, 2, -2.8f, prop), new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredHolder<Item, MultiversalShovelItem> PRISMATIC_SHOVEL = register("prismatic_shovel", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new MultiversalShovelItem(KALEIDITE_TOOLS, 2.5f, -3, prop), new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredHolder<Item, MultiversalAxeItem> PRISMATIC_AXE = register("prismatic_axe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), prop -> new MultiversalAxeItem(KALEIDITE_TOOLS, 6, -2.1f, prop), new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredHolder<Item, SimpleLoreItem> MULTIVERSAL_BEACON = register("multiversal_beacon", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), prop -> new SimpleLoreItem(true, ChatFormatting.GOLD, prop), new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredHolder<Item, ArmorItem> KALEIDITE_HELMET = register("kaleidite_helmet", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new ArmorItem(KALEIDITE_ARMOR, ArmorType.HELMET, prop), new Item.Properties());
    public static final DeferredHolder<Item, BeaconArmorItem> KALEIDITE_CHESTPLATE = register("kaleidite_chestplate", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new BeaconArmorItem(KALEIDITE_ARMOR, ArmorType.CHESTPLATE, prop), new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredHolder<Item, ArmorItem> KALEIDITE_LEGGINGS = register("kaleidite_leggings", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new ArmorItem(KALEIDITE_ARMOR, ArmorType.LEGGINGS, prop), new Item.Properties());
    public static final DeferredHolder<Item, ArmorItem> KALEIDITE_BOOTS = register("kaleidite_boots", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), prop -> new ArmorItem(KALEIDITE_ARMOR, ArmorType.BOOTS, prop), new Item.Properties());
    public static final DeferredHolder<Item, SummonCrossbowItem> KALEIDITE_CROSSBOW = register("kaleidite_crossbow", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), SummonCrossbowItem::new, new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredHolder<Item, SimpleTemplateItem> DIMENSIONAL_PRISM = register("dimensional_prism", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), prop -> new SimpleTemplateItem(true, Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimensional_prism.applies_to"))).withStyle(ChatFormatting.BLUE), Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimensional_prism.ingredients"))).withStyle(ChatFormatting.BLUE), Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "base_description"))), Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimensional_prism.additions_description"))), List.of(ResourceLocation.withDefaultNamespace("item/empty_slot_axe"), ResourceLocation.withDefaultNamespace("item/empty_slot_sword"), ResourceLocation.withDefaultNamespace("item/empty_slot_shovel"), ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe")), List.of(ResourceLocation.withDefaultNamespace("item/empty_slot_ingot")), prop), new Item.Properties());

    public static final DeferredHolder<Item, BlockItem> KALEIDITE_CLUSTER = register("kaleidite_cluster", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.NATURAL_BLOCKS), prop -> new BlockItem(BlockRegistry.KALEIDITE_CLUSTER.get(), prop), new Item.Properties());

    private static <T extends Item> DeferredHolder<Item, T> register(String name, List<ResourceKey<CreativeModeTab>> tab, Function<Item.Properties, T> item, Item.Properties base) {
        DeferredHolder<Item, T> out = ITEMS.register(name, () -> item.apply(base.setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, name)))));
        TABS.add(Pair.of(tab, out));
        return out;
    }

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        TABS.stream().filter(pair -> pair.getFirst().contains(event.getTabKey())).map(Pair::getSecond).forEach(item -> event.accept(item::get));
    }

}
