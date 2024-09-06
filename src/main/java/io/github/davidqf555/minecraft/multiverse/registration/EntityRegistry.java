package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.entities.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Multiverse.MOD_ID);

    public static final RegistryObject<EntityType<CollectorEntity>> COLLECTOR = register("collector", EntityType.Builder.of(CollectorEntity::new, MobCategory.MONSTER).sized(0.6f, 1.95f));
    public static final RegistryObject<EntityType<TravelerEntity>> TRAVELER = register("traveler", EntityType.Builder.of(TravelerEntity::new, MobCategory.MONSTER).sized(0.6f, 1.95f));
    public static final RegistryObject<EntityType<DoppelgangerEntity>> DOPPELGANGER = register("doppelganger", EntityType.Builder.of(DoppelgangerEntity::new, MobCategory.MISC).sized(0.6f, 1.95f));
    public static final RegistryObject<EntityType<KaleiditeCoreEntity>> KALEIDITE_CORE = register("kaleidite_core", EntityType.Builder.<KaleiditeCoreEntity>of(KaleiditeCoreEntity::new, MobCategory.MISC).sized(0.25f, 0.25f));
    public static final RegistryObject<EntityType<RiftEntity>> RIFT = register("rift", EntityType.Builder.of(RiftEntity::new, MobCategory.AMBIENT).sized(1, 1));

    private EntityRegistry() {
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> type) {
        return TYPES.register(name, () -> type.build(name));
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(COLLECTOR.get(), CollectorEntity.createAttributes().build());
        event.put(TRAVELER.get(), TravelerEntity.createAttributes().build());
        event.put(DOPPELGANGER.get(), DoppelgangerEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(TRAVELER.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TravelerEntity::canSpawn);
            SpawnPlacements.register(DOPPELGANGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DoppelgangerEntity::canSpawn);
        });
    }

}
