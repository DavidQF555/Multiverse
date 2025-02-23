package io.github.davidqf555.minecraft.multiverse.registration;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import io.github.davidqf555.minecraft.multiverse.common.world.items.*;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ItemRegistry {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Multiverse.MOD_ID);
    public static final SimpleTier KALEIDITE_TIER = new SimpleTier(BlockTags.INCORRECT_FOR_STONE_TOOL, 250, 4, 2, 22, () -> Ingredient.of(MultiverseTags.KALEIDITE_MATERIALS));
    private static final List<Pair<List<ResourceKey<CreativeModeTab>>, DeferredHolder<Item, ? extends Item>>> TABS = new LinkedList<>();
    public static final DeferredItem<RiftCoreItem> KALEIDITE_CORE = register("kaleidite_core", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), () -> new RiftCoreItem(ChatFormatting.LIGHT_PURPLE, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> KALEIDITE_SHARD = register("kaleidite_shard", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.INGREDIENTS), () -> new Item(new Item.Properties()));
    public static final DeferredItem<SwordItem> KALEIDITE_SWORD = register("kaleidite_sword", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new SwordItem(KALEIDITE_TIER, new Item.Properties().attributes(SwordItem.createAttributes(KALEIDITE_TIER, 3, -2.4f))));
    public static final DeferredItem<PickaxeItem> KALEIDITE_PICKAXE = register("kaleidite_pickaxe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new PickaxeItem(KALEIDITE_TIER, new Item.Properties().attributes(PickaxeItem.createAttributes(KALEIDITE_TIER, 1, -2.8f))));
    public static final DeferredItem<ShovelItem> KALEIDITE_SHOVEL = register("kaleidite_shovel", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new ShovelItem(KALEIDITE_TIER, new Item.Properties().attributes(ShovelItem.createAttributes(KALEIDITE_TIER, 1.5f, -3))));
    public static final DeferredItem<AxeItem> KALEIDITE_AXE = register("kaleidite_axe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new AxeItem(KALEIDITE_TIER, new Item.Properties().attributes(AxeItem.createAttributes(KALEIDITE_TIER, 6, -3.1f))));
    public static final DeferredItem<RiftSwordItem> PRISMATIC_SWORD = register("prismatic_sword", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new RiftSwordItem(KALEIDITE_TIER, new Item.Properties().rarity(Rarity.EPIC).attributes(SwordItem.createAttributes(KALEIDITE_TIER, 4, -2.4f))));
    public static final DeferredItem<MultiversalPickaxeItem> PRISMATIC_PICKAXE = register("prismatic_pickaxe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new MultiversalPickaxeItem(KALEIDITE_TIER, new Item.Properties().rarity(Rarity.EPIC).attributes(PickaxeItem.createAttributes(KALEIDITE_TIER, 2, -2.8f))));
    public static final DeferredItem<MultiversalShovelItem> PRISMATIC_SHOVEL = register("prismatic_shovel", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new MultiversalShovelItem(KALEIDITE_TIER, new Item.Properties().rarity(Rarity.EPIC).attributes(ShovelItem.createAttributes(KALEIDITE_TIER, 2.5f, -3))));
    public static final DeferredItem<MultiversalAxeItem> PRISMATIC_AXE = register("prismatic_axe", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new MultiversalAxeItem(KALEIDITE_TIER, new Item.Properties().rarity(Rarity.EPIC).attributes(AxeItem.createAttributes(KALEIDITE_TIER, 6, -2.1f))));
    public static final DeferredItem<SimpleLoreItem> MULTIVERSAL_BEACON = register("multiversal_beacon", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), () -> new SimpleLoreItem(true, ChatFormatting.GOLD, new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<ArmorItem> KALEIDITE_HELMET = register("kaleidite_helmet", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new ArmorItem(ArmorMaterialRegistry.KALEIDITE, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(33))));
    public static final DeferredItem<ArmorItem> KALEIDITE_CHESTPLATE = register("kaleidite_chestplate", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new ArmorItem(ArmorMaterialRegistry.KALEIDITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(33))));
    public static final DeferredItem<ArmorItem> KALEIDITE_LEGGINGS = register("kaleidite_leggings", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new ArmorItem(ArmorMaterialRegistry.KALEIDITE, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(33))));
    public static final DeferredItem<ArmorItem> KALEIDITE_BOOTS = register("kaleidite_boots", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new ArmorItem(ArmorMaterialRegistry.KALEIDITE, ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(33))));
    public static final DeferredItem<SummonCrossbowItem> BEACON_CROSSBOW = register("beacon_crossbow", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new SummonCrossbowItem(MultiverseTags.KALEIDITE_MATERIALS, new Item.Properties().stacksTo(1).durability(465).rarity(Rarity.EPIC)));
    public static final DeferredItem<SimpleLoreItem> DIMENSIONAL_PRISM = register("dimensional_prism", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), () -> new SimpleLoreItem(true, ChatFormatting.GOLD, new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<BeaconArmorItem> BEACON_CHESTPLATE = register("beacon_chestplate", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new BeaconArmorItem(ArmorMaterialRegistry.BEACON, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(33)).rarity(Rarity.EPIC)));
    public static final DeferredItem<WarpShieldItem> WARP_SHIELD = register("warp_shield", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.COMBAT), () -> new WarpShieldItem(MultiverseTags.KALEIDITE_MATERIALS, ChatFormatting.GOLD, new Item.Properties().durability(336).rarity(Rarity.EPIC)));
    public static final DeferredItem<WarpToolItem> WARP_RING = register("warp_ring", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), () -> new WarpToolItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredItem<WarpStickItem> WARP_STICK = register("warp_stick", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey()), () -> new WarpStickItem(ChatFormatting.GOLD, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredItem<DeferredSpawnEggItem> TRAVELER_SPAWN_EGG = register("traveler_spawn_egg", List.of(CreativeModeTabs.SPAWN_EGGS), () -> new DeferredSpawnEggItem(EntityRegistry.TRAVELER, 0x5BE6FF, 0x4A6CF7, new Item.Properties()));
    public static final DeferredItem<DeferredSpawnEggItem> CONQUEROR_SPAWN_EGG = register("conqueror_spawn_egg", List.of(CreativeModeTabs.SPAWN_EGGS), () -> new DeferredSpawnEggItem(EntityRegistry.CONQUEROR, 0x5BE6FF, 0xE0B230, new Item.Properties()));

    public static final DeferredItem<BlockItem> KALEIDITE_CLUSTER = register("kaleidite_cluster", List.of(CreativeModeTabRegistry.MULTIVERSE.getKey(), CreativeModeTabs.NATURAL_BLOCKS), () -> new BlockItem(BlockRegistry.KALEIDITE_CLUSTER.get(), new Item.Properties()));

    private static <T extends Item> DeferredItem<T> register(String name, List<ResourceKey<CreativeModeTab>> tab, Supplier<T> item) {
        DeferredItem<T> out = ITEMS.register(name, item);
        TABS.add(Pair.of(tab, out));
        return out;
    }

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        TABS.stream().filter(pair -> pair.getFirst().contains(event.getTabKey())).map(Pair::getSecond).forEach(item -> event.accept(item::get));
    }

}
