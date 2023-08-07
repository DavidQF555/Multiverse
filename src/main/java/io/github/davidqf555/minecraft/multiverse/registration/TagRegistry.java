package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public final class TagRegistry {

    public static final TagKey<Item> DOPPELGANGER_HEAD = TagKey.create(Registries.ITEM, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/head"));
    public static final TagKey<Item> DOPPELGANGER_CHEST = TagKey.create(Registries.ITEM, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/chest"));
    public static final TagKey<Item> DOPPELGANGER_LEGS = TagKey.create(Registries.ITEM, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/legs"));
    public static final TagKey<Item> DOPPELGANGER_FEET = TagKey.create(Registries.ITEM, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/feet"));
    public static final TagKey<Item> DOPPELGANGER_MAIN_HAND = TagKey.create(Registries.ITEM, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/main_hand"));
    public static final TagKey<Item> DOPPELGANGER_OFF_HAND = TagKey.create(Registries.ITEM, new ResourceLocation(Multiverse.MOD_ID, "doppelganger/off_hand"));

    public static final TagKey<EntityType<?>> ALWAYS_GENERATE_MULTIVERSE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Multiverse.MOD_ID, "always_generate_multiverse"));

    private TagRegistry() {
    }

}
