package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.entities.CollectorEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Multiverse.MOD_ID);

    public static final RegistryObject<EntityType<CollectorEntity>> COLLECTOR = register("collector", EntityType.Builder.of(new CollectorEntity.Factory(), EntityClassification.MONSTER).sized(0.6f, 1.95f));

    private EntityRegistry() {
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> type) {
        return TYPES.register(name, () -> type.build(name));
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.COLLECTOR.get(), CollectorEntity.createAttributes().build());
    }
}
