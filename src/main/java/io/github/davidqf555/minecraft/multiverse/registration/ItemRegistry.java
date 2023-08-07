package io.github.davidqf555.minecraft.multiverse.registration;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.items.*;
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
    private static final List<Pair<Supplier<CreativeModeTab>, RegistryObject<? extends Item>>> TABS = new LinkedList<>();

    private static CreativeModeTab tab;

    public static final RegistryObject<RiftSwordItem> BOUNDLESS_BLADE = register("boundless_blade", ItemRegistry::getTab, () -> new RiftSwordItem(KaleiditeItemTier.INSTANCE, 2, -2.4f, new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<SpawnCollectorItem> UNIVERSAL_TREASURE = register("universal_treasure", ItemRegistry::getTab, () -> new SpawnCollectorItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1).fireResistant(), 300));
    public static final RegistryObject<RiftDeathItem> TOTEM_OF_ESCAPE = register("totem_of_escape", ItemRegistry::getTab, () -> new RiftDeathItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), 5));
    public static final RegistryObject<RiftCoreItem> KALEIDITE_CORE = register("kaleidite_core", ItemRegistry::getTab, () -> new RiftCoreItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> KALEIDITE_SHARD = register("kaleidite_shard", ItemRegistry::getTab, () -> new Item(new Item.Properties()));
    public static final RegistryObject<SimpleFoiledItem> MULTIVERSAL_BEACON = register("multiversal_beacon", ItemRegistry::getTab, () -> new SimpleFoiledItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<ArmorItem> KALEIDITE_HELMET = register("kaleidite_helmet", ItemRegistry::getTab, () -> new ArmorItem(KaleiditeArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<BeaconArmorItem> KALEIDITE_CHESTPLATE = register("kaleidite_chestplate", ItemRegistry::getTab, () -> new BeaconArmorItem(KaleiditeArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<ArmorItem> KALEIDITE_LEGGINGS = register("kaleidite_leggings", ItemRegistry::getTab, () -> new ArmorItem(KaleiditeArmorMaterial.INSTANCE, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<ArmorItem> KALEIDITE_BOOTS = register("kaleidite_boots", ItemRegistry::getTab, () -> new ArmorItem(KaleiditeArmorMaterial.INSTANCE, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<BlockItem> KALEIDITE_CLUSTER = register("kaleidite_cluster", ItemRegistry::getTab, () -> new BlockItem(BlockRegistry.KALEIDITE_CLUSTER.get(), new Item.Properties()));

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<CreativeModeTab> tab, Supplier<T> item) {
        RegistryObject<T> out = ITEMS.register(name, item);
        TABS.add(Pair.of(tab, out));
        return out;
    }

    @SubscribeEvent
    public static void onBuildContents(CreativeModeTabEvent.BuildContents event) {
        TABS.stream().filter(pair -> pair.getFirst().get().equals(event.getTab())).map(Pair::getSecond).forEach(item -> event.accept(item::get));
    }

    @SubscribeEvent
    public static void onRegisterCreativeModeTab(CreativeModeTabEvent.Register event) {
        tab = event.registerCreativeModeTab(new ResourceLocation(Multiverse.MOD_ID, "main"), builder -> builder.icon(() -> KALEIDITE_SHARD.get().getDefaultInstance()));
    }

    public static CreativeModeTab getTab() {
        return tab;
    }

}
