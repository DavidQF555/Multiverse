package io.github.davidqf555.minecraft.multiverse.common;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class MultiverseTags {

    public static final TagKey<Item> DOPPELGANGER_HEAD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/head"));
    public static final TagKey<Item> DOPPELGANGER_CHEST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/chest"));
    public static final TagKey<Item> DOPPELGANGER_LEGS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/legs"));
    public static final TagKey<Item> DOPPELGANGER_FEET = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/feet"));
    public static final TagKey<Item> DOPPELGANGER_MAIN_HAND = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/main_hand"));
    public static final TagKey<Item> DOPPELGANGER_OFF_HAND = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/off_hand"));
    public static final TagKey<Item> KALEIDITE_MATERIALS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "kaleidite_materials"));

    public static final TagKey<EntityType<?>> IGNORE_RIFT = TagKey.create(ForgeRegistries.Keys.ENTITY_TYPES, new ResourceLocation(Multiverse.MOD_ID, "ignore_rift"));

    private MultiverseTags() {
    }

}
