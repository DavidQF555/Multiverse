package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.ConquerorEntity;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.DoppelgangerEntity;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.KaleiditeCoreEntity;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.TravelerEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Multiverse.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<TravelerEntity>> TRAVELER = register("traveler", EntityType.Builder.of(TravelerEntity::new, MobCategory.MONSTER).sized(0.6f, 1.95f));
    public static final DeferredHolder<EntityType<?>, EntityType<DoppelgangerEntity>> DOPPELGANGER = register("doppelganger", EntityType.Builder.of(DoppelgangerEntity::new, MobCategory.MISC).sized(0.6f, 1.95f));
    public static final DeferredHolder<EntityType<?>, EntityType<KaleiditeCoreEntity>> KALEIDITE_CORE = register("kaleidite_core", EntityType.Builder.<KaleiditeCoreEntity>of(KaleiditeCoreEntity::new, MobCategory.MISC).sized(0.25f, 0.25f));
    public static final DeferredHolder<EntityType<?>, EntityType<ConquerorEntity>> CONQUEROR = register("conqueror", EntityType.Builder.of(ConquerorEntity::new, MobCategory.MONSTER).sized(0.6f, 1.95f));

    private EntityRegistry() {
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.Builder<T> type) {
        return TYPES.register(name, () -> type.build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, name))));
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(TRAVELER.get(), TravelerEntity.createAttributes().build());
        event.put(DOPPELGANGER.get(), DoppelgangerEntity.createAttributes().build());
        event.put(CONQUEROR.get(), ConquerorEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterSpawnPlacement(RegisterSpawnPlacementsEvent event) {
        event.register(DOPPELGANGER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DoppelgangerEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
    }

}
