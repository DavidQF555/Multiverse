package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.items.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Multiverse.MOD_ID);

    public static final RegistryObject<RiftSwordItem> BOUNDLESS_BLADE = register("boundless_blade", () -> new RiftSwordItem(KaleiditeItemTier.INSTANCE, 2, -2.4f, new Item.Properties().rarity(Rarity.EPIC).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<SpawnCollectorItem> UNIVERSAL_TREASURE = register("universal_treasure", () -> new SpawnCollectorItem(new Item.Properties().rarity(Rarity.RARE).tab(CreativeModeTab.TAB_MISC).stacksTo(1).fireResistant(), 300));
    public static final RegistryObject<RiftDeathItem> TOTEM_OF_ESCAPE = register("totem_of_escape", () -> new RiftDeathItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC), 5));
    public static final RegistryObject<RiftCoreItem> KALEIDITE_CORE = register("kaleidite_core", () -> new RiftCoreItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> KALEIDITE = register("kaleidite", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<BlockItem> KALEIDITE_CLUSTER = register("kaleidite_cluster", () -> new BlockItem(BlockRegistry.KALEIDITE_CLUSTER.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    private ItemRegistry() {
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
