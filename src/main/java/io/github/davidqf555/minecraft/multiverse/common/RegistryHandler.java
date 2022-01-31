package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.entities.DimensionBossEntity;
import io.github.davidqf555.minecraft.multiverse.common.items.DimensionSlasherItem;
import io.github.davidqf555.minecraft.multiverse.common.items.UniversalTreasureItem;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.RiftFeature;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RegistryHandler {

    public static final RegistryObject<RiftBlock> RIFT_BLOCK = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "rift"), ForgeRegistries.BLOCKS);
    public static final RegistryObject<TileEntityType<RiftTileEntity>> RIFT_TILE_ENTITY_TYPE = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "rift"), ForgeRegistries.TILE_ENTITIES);
    public static final RegistryObject<PointOfInterestType> RIFT_POI_TYPE = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "rift"), ForgeRegistries.POI_TYPES);
    public static final RegistryObject<RiftFeature> RIFT_FEATURE = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "rift"), ForgeRegistries.FEATURES);
    public static final RegistryObject<DimensionSlasherItem> DIMENSION_SLASHER_ITEM = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "dimension_slasher"), ForgeRegistries.ITEMS);
    public static final RegistryObject<Item> UNIVERSAL_TREASURE_ITEM = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "universal_treasure"), ForgeRegistries.ITEMS);
    public static final RegistryObject<EntityType<DimensionBossEntity>> DIMENSION_BOSS_ENTITY = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "dimension_boss"), ForgeRegistries.ENTITIES);

    private RegistryHandler() {
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new RiftBlock().setRegistryName(RIFT_BLOCK.getId()));
    }

    @SubscribeEvent
    public static void registerTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.of(RiftTileEntity::new, RIFT_BLOCK.get()).build(Util.fetchChoiceType(TypeReferences.BLOCK_ENTITY, RIFT_TILE_ENTITY_TYPE.getId().getPath())).setRegistryName(RIFT_TILE_ENTITY_TYPE.getId()));
    }

    @SubscribeEvent
    public static void registerPOITypes(RegistryEvent.Register<PointOfInterestType> event) {
        event.getRegistry().register(new PointOfInterestType(RIFT_POI_TYPE.getId().getPath(), PointOfInterestType.getBlockStates(RIFT_BLOCK.get()), 0, 1).setRegistryName(RIFT_POI_TYPE.getId()));
    }

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(RiftFeature.INSTANCE.setRegistryName(RIFT_FEATURE.getId()));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new DimensionSlasherItem().setRegistryName(DIMENSION_SLASHER_ITEM.getId()),
                new UniversalTreasureItem().setRegistryName(UNIVERSAL_TREASURE_ITEM.getId())
        );
    }

    @SubscribeEvent
    public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(EntityType.Builder.of(new DimensionBossEntity.Factory(), EntityClassification.MONSTER).sized(0.6f, 1.95f).build(DIMENSION_BOSS_ENTITY.getId().toString()).setRegistryName(DIMENSION_BOSS_ENTITY.getId()));
    }
}
