package io.github.davidqf555.minecraft.multiverse.datagen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class DataGenRegistry {

    private DataGenRegistry() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(true, new DatapackBuiltinEntriesProvider(gen.getPackOutput(), event.getLookupProvider(), new RegistrySetBuilder().add(Registries.NOISE_SETTINGS, DataGenRegistry::registerNoiseGeneratorSettings), Set.of(Multiverse.MOD_ID)));
        gen.addProvider(true, new DimensionTypesGenerator(gen.getPackOutput(), event.getLookupProvider(), Multiverse.MOD_ID, event.getExistingFileHelper()));
    }

    private static void registerNoiseGeneratorSettings(BootstrapContext<NoiseGeneratorSettings> context) {
        for (MultiverseShape shape : MultiverseShape.values()) {
            for (MultiverseType type : MultiverseType.values()) {
                context.register(shape.getNoiseSettingsKey(type), shape.createNoiseSettings(context, type));
            }
        }
    }

}
