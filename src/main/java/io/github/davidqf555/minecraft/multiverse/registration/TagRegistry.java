package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class TagRegistry {

    public static final TagKey<Item> DOPPELGANGER_HEAD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/head"));
    public static final TagKey<Item> DOPPELGANGER_CHEST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/chest"));
    public static final TagKey<Item> DOPPELGANGER_LEGS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/legs"));
    public static final TagKey<Item> DOPPELGANGER_FEET = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/feet"));
    public static final TagKey<Item> DOPPELGANGER_MAIN_HAND = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/main_hand"));
    public static final TagKey<Item> DOPPELGANGER_OFF_HAND = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/off_hand"));

    private TagRegistry() {
    }

}
