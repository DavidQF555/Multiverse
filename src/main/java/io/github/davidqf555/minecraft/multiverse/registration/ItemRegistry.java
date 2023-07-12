package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.items.BoundlessBladeItem;
import io.github.davidqf555.minecraft.multiverse.common.items.CrystalItemTier;
import io.github.davidqf555.minecraft.multiverse.common.items.FabricOfRealityItem;
import io.github.davidqf555.minecraft.multiverse.common.items.UniversalTreasureItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Multiverse.MOD_ID);

    public static final RegistryObject<BoundlessBladeItem> BOUNDLESS_BLADE = register("boundless_blade", () -> new BoundlessBladeItem(CrystalItemTier.INSTANCE, 2, -2.4f, new Item.Properties().rarity(Rarity.EPIC).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<UniversalTreasureItem> UNIVERSAL_TREASURE = register("universal_treasure", () -> new UniversalTreasureItem(new Item.Properties().rarity(Rarity.RARE).tab(CreativeModeTab.TAB_MISC).stacksTo(1).fireResistant()));
    public static final RegistryObject<FabricOfRealityItem> FABRIC_OF_REALITY = register("fabric_of_reality", () -> new FabricOfRealityItem(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS).rarity(Rarity.UNCOMMON)));

    private ItemRegistry() {
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
