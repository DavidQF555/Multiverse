package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.entities.CollectorEntity;
import io.github.davidqf555.minecraft.multiverse.common.items.BoundlessBladeItem;
import io.github.davidqf555.minecraft.multiverse.common.items.FabricOfRealityItem;
import io.github.davidqf555.minecraft.multiverse.common.items.UniversalTreasureItem;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.rifts.RiftFeature;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RegistryHandler {

    public static final RegistryObject<RiftBlock> RIFT_BLOCK = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "rift"), ForgeRegistries.BLOCKS);
    public static final RegistryObject<BlockEntityType<RiftTileEntity>> RIFT_TILE_ENTITY_TYPE = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "rift"), ForgeRegistries.BLOCK_ENTITIES);
    public static final RegistryObject<PoiType> RIFT_POI_TYPE = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "rift"), ForgeRegistries.POI_TYPES);
    public static final RegistryObject<RiftFeature> RIFT_FEATURE = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "rift"), ForgeRegistries.FEATURES);
    public static final RegistryObject<BoundlessBladeItem> BOUNDLESS_BLADE_ITEM = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "boundless_blade"), ForgeRegistries.ITEMS);
    public static final RegistryObject<UniversalTreasureItem> UNIVERSAL_TREASURE_ITEM = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "universal_treasure"), ForgeRegistries.ITEMS);
    public static final RegistryObject<FabricOfRealityItem> FABRIC_OF_REALITY_ITEM = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "fabric_of_reality"), ForgeRegistries.ITEMS);
    public static final RegistryObject<EntityType<CollectorEntity>> COLLECTOR_ENTITY = RegistryObject.of(new ResourceLocation(Multiverse.MOD_ID, "collector"), ForgeRegistries.ENTITIES);

    private RegistryHandler() {
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new RiftBlock().setRegistryName(RIFT_BLOCK.getId()));
    }

    @SubscribeEvent
    public static void registerTileEntityTypes(RegistryEvent.Register<BlockEntityType<?>> event) {
        event.getRegistry().register(BlockEntityType.Builder.of(RiftTileEntity::new, RIFT_BLOCK.get()).build(Util.fetchChoiceType(References.BLOCK_ENTITY, RIFT_TILE_ENTITY_TYPE.getId().getPath())).setRegistryName(RIFT_TILE_ENTITY_TYPE.getId()));
    }

    @SubscribeEvent
    public static void registerPOITypes(RegistryEvent.Register<PoiType> event) {
        event.getRegistry().register(new PoiType(RIFT_POI_TYPE.getId().getPath(), PoiType.getBlockStates(RIFT_BLOCK.get()), 0, 1).setRegistryName(RIFT_POI_TYPE.getId()));
    }

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(RiftFeature.INSTANCE.setRegistryName(RIFT_FEATURE.getId()));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new BoundlessBladeItem().setRegistryName(BOUNDLESS_BLADE_ITEM.getId()),
                new UniversalTreasureItem().setRegistryName(UNIVERSAL_TREASURE_ITEM.getId()),
                new FabricOfRealityItem().setRegistryName(FABRIC_OF_REALITY_ITEM.getId())
        );
    }

    @SubscribeEvent
    public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(EntityType.Builder.of(new CollectorEntity.Factory(), MobCategory.MONSTER).sized(0.6f, 1.95f).build(COLLECTOR_ENTITY.getId().toString()).setRegistryName(COLLECTOR_ENTITY.getId()));
    }
}
