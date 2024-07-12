package io.github.davidqf555.minecraft.multiverse.registration;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Multiverse.MOD_ID);
    private static final List<Pair<List<ResourceKey<CreativeModeTab>>, DeferredHolder<Item, ? extends Item>>> TABS = new LinkedList<>();
    public static final DeferredHolder<Item, SpawnCollectorItem> UNIVERSAL_TREASURE = register("universal_treasure", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), () -> new SpawnCollectorItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1).fireResistant(), 300));
    public static final DeferredHolder<Item, RiftDeathItem> TOTEM_OF_ESCAPE = register("totem_of_escape", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), () -> new RiftDeathItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), 5));
    public static final DeferredHolder<Item, RiftCoreItem> KALEIDITE_CORE = register("kaleidite_core", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), () -> new RiftCoreItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredHolder<Item, Item> KALEIDITE_SHARD = register("kaleidite_shard", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.INGREDIENTS), () -> new Item(new Item.Properties()));
    public static final SimpleTier KALEIDITE_TIER = new SimpleTier(BlockTags.INCORRECT_FOR_STONE_TOOL, 250, 4, 2, 22, () -> Ingredient.of(ItemRegistry.KALEIDITE_SHARD::get));
    public static final DeferredHolder<Item, SwordItem> KALEIDITE_SWORD = register("kaleidite_sword", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new SwordItem(KALEIDITE_TIER, new Item.Properties().attributes(SwordItem.createAttributes(KALEIDITE_TIER, 3, -2.4f))));
    public static final DeferredHolder<Item, PickaxeItem> KALEIDITE_PICKAXE = register("kaleidite_pickaxe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new PickaxeItem(KALEIDITE_TIER, new Item.Properties().attributes(PickaxeItem.createAttributes(KALEIDITE_TIER, 1, -2.8f))));
    public static final DeferredHolder<Item, ShovelItem> KALEIDITE_SHOVEL = register("kaleidite_shovel", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new ShovelItem(KALEIDITE_TIER, new Item.Properties().attributes(ShovelItem.createAttributes(KALEIDITE_TIER, 1.5f, -3))));
    public static final DeferredHolder<Item, AxeItem> KALEIDITE_AXE = register("kaleidite_axe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new AxeItem(KALEIDITE_TIER, new Item.Properties().attributes(AxeItem.createAttributes(KALEIDITE_TIER, 6, -3.1f))));
    public static final DeferredHolder<Item, RiftSwordItem> PRISMATIC_SWORD = register("prismatic_sword", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new RiftSwordItem(KALEIDITE_TIER, new Item.Properties().rarity(Rarity.EPIC).attributes(SwordItem.createAttributes(KALEIDITE_TIER, 4, -2.4f))));
    public static final DeferredHolder<Item, MultiversalPickaxeItem> PRISMATIC_PICKAXE = register("prismatic_pickaxe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new MultiversalPickaxeItem(KALEIDITE_TIER, new Item.Properties().rarity(Rarity.EPIC).attributes(PickaxeItem.createAttributes(KALEIDITE_TIER, 2, -2.8f))));
    public static final DeferredHolder<Item, MultiversalShovelItem> PRISMATIC_SHOVEL = register("prismatic_shovel", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new MultiversalShovelItem(KALEIDITE_TIER, new Item.Properties().rarity(Rarity.EPIC).attributes(ShovelItem.createAttributes(KALEIDITE_TIER, 2.5f, -3))));
    public static final DeferredHolder<Item, MultiversalAxeItem> PRISMATIC_AXE = register("prismatic_axe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new MultiversalAxeItem(KALEIDITE_TIER, new Item.Properties().rarity(Rarity.EPIC).attributes(AxeItem.createAttributes(KALEIDITE_TIER, 6, -2.1f))));
    public static final DeferredHolder<Item, SimpleLoreItem> MULTIVERSAL_BEACON = register("multiversal_beacon", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), () -> new SimpleLoreItem(true, ChatFormatting.GOLD, new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, ArmorItem> KALEIDITE_HELMET = register("kaleidite_helmet", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new ArmorItem(ArmorMaterialRegistry.KALEIDITE, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final DeferredHolder<Item, BeaconArmorItem> KALEIDITE_CHESTPLATE = register("kaleidite_chestplate", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new BeaconArmorItem(ArmorMaterialRegistry.KALEIDITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, ArmorItem> KALEIDITE_LEGGINGS = register("kaleidite_leggings", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new ArmorItem(ArmorMaterialRegistry.KALEIDITE, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final DeferredHolder<Item, ArmorItem> KALEIDITE_BOOTS = register("kaleidite_boots", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new ArmorItem(ArmorMaterialRegistry.KALEIDITE, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final DeferredHolder<Item, SummonCrossbowItem> KALEIDITE_CROSSBOW = register("kaleidite_crossbow", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new SummonCrossbowItem(new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, SimpleTemplateItem> DIMENSIONAL_PRISM = register("dimensional_prism", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), () -> new SimpleTemplateItem(true, Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimensional_prism.applies_to"))).withStyle(ChatFormatting.BLUE), Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimensional_prism.ingredients"))).withStyle(ChatFormatting.BLUE), Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimensional_prism.lore"))).withStyle(ChatFormatting.GOLD), Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "base_description"))), Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "dimensional_prism.additions_description"))), List.of(ResourceLocation.withDefaultNamespace("item/empty_slot_axe"), ResourceLocation.withDefaultNamespace("item/empty_slot_sword"), ResourceLocation.withDefaultNamespace("item/empty_slot_shovel"), ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe")), List.of(ResourceLocation.withDefaultNamespace("item/empty_slot_ingot"))));

    public static final DeferredHolder<Item, BlockItem> KALEIDITE_CLUSTER = register("kaleidite_cluster", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.NATURAL_BLOCKS), () -> new BlockItem(BlockRegistry.KALEIDITE_CLUSTER.get(), new Item.Properties()));

    private static <T extends Item> DeferredHolder<Item, T> register(String name, List<ResourceKey<CreativeModeTab>> tab, Supplier<T> item) {
        DeferredHolder<Item, T> out = ITEMS.register(name, item);
        TABS.add(Pair.of(tab, out));
        return out;
    }

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        TABS.stream().filter(pair -> pair.getFirst().contains(event.getTabKey())).map(Pair::getSecond).forEach(item -> event.accept(item::get));
    }

}
