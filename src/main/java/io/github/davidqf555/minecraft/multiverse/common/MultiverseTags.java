package io.github.davidqf555.minecraft.multiverse.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public final class MultiverseTags {

    public static final TagKey<Item> DOPPELGANGER_HEAD = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "doppelganger/head"));
    public static final TagKey<Item> DOPPELGANGER_CHEST = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "doppelganger/chest"));
    public static final TagKey<Item> DOPPELGANGER_LEGS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "doppelganger/legs"));
    public static final TagKey<Item> DOPPELGANGER_FEET = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "doppelganger/feet"));
    public static final TagKey<Item> DOPPELGANGER_BODY = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "doppelganger/body"));
    public static final TagKey<Item> DOPPELGANGER_MAIN_HAND = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "doppelganger/main_hand"));
    public static final TagKey<Item> DOPPELGANGER_OFF_HAND = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "doppelganger/off_hand"));

    public static final TagKey<EntityType<?>> GENERATE_MULTIVERSE = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "generate_multiverse"));

    private MultiverseTags() {
    }

}
