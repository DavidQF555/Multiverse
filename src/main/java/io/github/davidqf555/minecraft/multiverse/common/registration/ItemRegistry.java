package io.github.davidqf555.minecraft.multiverse.common.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.items.BoundlessBladeItem;
import io.github.davidqf555.minecraft.multiverse.common.items.FabricOfRealityItem;
import io.github.davidqf555.minecraft.multiverse.common.items.UniversalTreasureItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Multiverse.MOD_ID);

    public static final RegistryObject<BoundlessBladeItem> BOUNDLESS_BLADE = register("boundless_blade", BoundlessBladeItem::new);
    public static final RegistryObject<UniversalTreasureItem> UNIVERSAL_TREASURE = register("universal_treasure", UniversalTreasureItem::new);
    public static final RegistryObject<FabricOfRealityItem> FABRIC_OF_REALITY = register("fabric_of_reality", FabricOfRealityItem::new);

    private ItemRegistry() {
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
