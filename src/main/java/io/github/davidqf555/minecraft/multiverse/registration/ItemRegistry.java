package io.github.davidqf555.minecraft.multiverse.registration;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.items.*;
import io.github.davidqf555.minecraft.multiverse.common.items.tools.MultiversalAxeItem;
import io.github.davidqf555.minecraft.multiverse.common.items.tools.MultiversalPickaxeItem;
import io.github.davidqf555.minecraft.multiverse.common.items.tools.MultiversalShovelItem;
import io.github.davidqf555.minecraft.multiverse.common.items.tools.RiftSwordItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Multiverse.MOD_ID);
    private static final List<Pair<Supplier<List<CreativeModeTab>>, RegistryObject<? extends Item>>> TABS = new LinkedList<>();

    private static CreativeModeTab tab;

    public static final RegistryObject<SpawnCollectorItem> UNIVERSAL_TREASURE = register("universal_treasure", () -> List.of(ItemRegistry.getTab()), () -> new SpawnCollectorItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1).fireResistant(), 300));
    public static final RegistryObject<RiftDeathItem> TOTEM_OF_ESCAPE = register("totem_of_escape", () -> List.of(ItemRegistry.getTab()), () -> new RiftDeathItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), 5));
    public static final RegistryObject<RiftCoreItem> KALEIDITE_CORE = register("kaleidite_core", () -> List.of(ItemRegistry.getTab()), () -> new RiftCoreItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> KALEIDITE_SHARD = register("kaleidite_shard", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.INGREDIENTS), () -> new Item(new Item.Properties()));
    public static final RegistryObject<SimpleLoreItem> MULTIVERSAL_BEACON = register("multiversal_beacon", () -> List.of(ItemRegistry.getTab()), () -> new SimpleLoreItem(true, ChatFormatting.GOLD, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<ArmorItem> KALEIDITE_HELMET = register("kaleidite_helmet", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.COMBAT), () -> new ArmorItem(KaleiditeArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<BeaconArmorItem> KALEIDITE_CHESTPLATE = register("kaleidite_chestplate", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.COMBAT), () -> new BeaconArmorItem(KaleiditeArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<ArmorItem> KALEIDITE_LEGGINGS = register("kaleidite_leggings", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.COMBAT), () -> new ArmorItem(KaleiditeArmorMaterial.INSTANCE, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<ArmorItem> KALEIDITE_BOOTS = register("kaleidite_boots", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.COMBAT), () -> new ArmorItem(KaleiditeArmorMaterial.INSTANCE, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final RegistryObject<SummonCrossbowItem> KALEIDITE_CROSSBOW = register("kaleidite_crossbow", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.COMBAT), () -> new SummonCrossbowItem(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<SwordItem> KALEIDITE_SWORD = register("kaleidite_sword", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.COMBAT), () -> new SwordItem(KaleiditeItemTier.INSTANCE, 3, -2.4f, new Item.Properties()));
    public static final RegistryObject<PickaxeItem> KALEIDITE_PICKAXE = register("kaleidite_pickaxe", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new PickaxeItem(KaleiditeItemTier.INSTANCE, 1, -2.8f, new Item.Properties()));
    public static final RegistryObject<ShovelItem> KALEIDITE_SHOVEL = register("kaleidite_shovel", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new ShovelItem(KaleiditeItemTier.INSTANCE, 1.5f, -3, new Item.Properties()));
    public static final RegistryObject<AxeItem> KALEIDITE_AXE = register("kaleidite_axe", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new AxeItem(KaleiditeItemTier.INSTANCE, 6, -3.1f, new Item.Properties()));
    public static final RegistryObject<RiftSwordItem> PRISMATIC_SWORD = register("prismatic_sword", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.COMBAT), () -> new RiftSwordItem(KaleiditeItemTier.INSTANCE, 4, -2.4f, new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<MultiversalPickaxeItem> PRISMATIC_PICKAXE = register("prismatic_pickaxe", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new MultiversalPickaxeItem(KaleiditeItemTier.INSTANCE, 2, -2.8f, new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<MultiversalShovelItem> PRISMATIC_SHOVEL = register("prismatic_shovel", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new MultiversalShovelItem(KaleiditeItemTier.INSTANCE, 2.5f, -3, new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<MultiversalAxeItem> PRISMATIC_AXE = register("prismatic_axe", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.TOOLS_AND_UTILITIES), () -> new MultiversalAxeItem(KaleiditeItemTier.INSTANCE, 6, -2.1f, new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<SimpleLoreItem> DIMENSIONAL_PRISM = register("dimensional_prism", () -> List.of(ItemRegistry.getTab()), () -> new SimpleLoreItem(true, ChatFormatting.GOLD, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<BlockItem> KALEIDITE_CLUSTER = register("kaleidite_cluster", () -> List.of(ItemRegistry.getTab(), CreativeModeTabs.NATURAL_BLOCKS), () -> new BlockItem(BlockRegistry.KALEIDITE_CLUSTER.get(), new Item.Properties()));

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<List<CreativeModeTab>> tab, Supplier<T> item) {
        RegistryObject<T> out = ITEMS.register(name, item);
        TABS.add(Pair.of(tab, out));
        return out;
    }

    @SubscribeEvent
    public static void onBuildContents(CreativeModeTabEvent.BuildContents event) {
        TABS.stream().filter(pair -> pair.getFirst().get().contains(event.getTab())).map(Pair::getSecond).forEach(item -> event.accept(item::get));
    }

    @SubscribeEvent
    public static void onRegisterCreativeModeTab(CreativeModeTabEvent.Register event) {
        tab = event.registerCreativeModeTab(new ResourceLocation(Multiverse.MOD_ID, "main"), builder -> builder.icon(() -> KALEIDITE_SHARD.get().getDefaultInstance()).title(Component.translatable("itemGroup." + Multiverse.MOD_ID)));
    }

    public static CreativeModeTab getTab() {
        return tab;
    }

}
