package io.github.davidqf555.minecraft.multiverse.registration;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.items.BoundlessBladeItem;
import io.github.davidqf555.minecraft.multiverse.common.items.CrystalItemTier;
import io.github.davidqf555.minecraft.multiverse.common.items.FabricOfRealityItem;
import io.github.davidqf555.minecraft.multiverse.common.items.UniversalTreasureItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
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
    private static final List<Pair<Supplier<CreativeModeTab>, RegistryObject<? extends Item>>> TABS = new LinkedList<>();

    public static final RegistryObject<BoundlessBladeItem> BOUNDLESS_BLADE = register("boundless_blade", () -> CreativeModeTabs.COMBAT, () -> new BoundlessBladeItem(CrystalItemTier.INSTANCE, 2, -2.4f, new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<UniversalTreasureItem> UNIVERSAL_TREASURE = register("universal_treasure", () -> CreativeModeTabs.INGREDIENTS, () -> new UniversalTreasureItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1).fireResistant()));
    public static final RegistryObject<FabricOfRealityItem> FABRIC_OF_REALITY = register("fabric_of_reality", () -> CreativeModeTabs.INGREDIENTS, () -> new FabricOfRealityItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

    private ItemRegistry() {
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<CreativeModeTab> tab, Supplier<T> item) {
        RegistryObject<T> out = ITEMS.register(name, item);
        TABS.add(Pair.of(tab, out));
        return out;
    }

    @SubscribeEvent
    public static void onBuildContents(CreativeModeTabEvent.BuildContents event) {
        TABS.stream().filter(pair -> pair.getFirst().get().equals(event.getTab())).map(Pair::getSecond).forEach(item -> event.accept(item::get));
    }

}
