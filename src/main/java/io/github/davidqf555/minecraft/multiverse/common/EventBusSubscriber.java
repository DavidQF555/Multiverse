package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.common.entities.CollectorEntity;
import io.github.davidqf555.minecraft.multiverse.common.packets.UpdateClientDimensionsPacket;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.DynamicDefaultChunkGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.rifts.RiftFeature;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.rifts.RiftPlacement;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class EventBusSubscriber {

    private EventBusSubscriber() {
    }

    @Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeBus {

        private ForgeBus() {
        }

        @SubscribeEvent
        public static void onBiomeLoading(BiomeLoadingEvent event) {
            event.getGeneration().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, RiftFeature.PLACED);
        }

    }

    @Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBus {

        private static int index = 0;

        private ModBus() {
        }

        @SubscribeEvent
        public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                UpdateClientDimensionsPacket.register(index++);
                Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Multiverse.MOD_ID, "dynamic_default"), DynamicDefaultChunkGenerator.CODEC);
                RiftPlacement.TYPE = Registry.register(Registry.PLACEMENT_MODIFIERS, RiftPlacement.LOCATION, () -> RiftPlacement.CODEC);
            });
        }

        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(RegistryHandler.COLLECTOR_ENTITY.get(), CollectorEntity.createAttributes().build());
        }
    }
}
