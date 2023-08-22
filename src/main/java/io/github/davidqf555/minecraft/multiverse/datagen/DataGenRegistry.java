package io.github.davidqf555.minecraft.multiverse.datagen;

import com.mojang.serialization.JsonOps;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.*;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenRegistry {

    private DataGenRegistry() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(true, new DatapackBuiltinEntriesProvider(gen.getPackOutput(), event.getLookupProvider(), new RegistrySetBuilder().add(Registries.NOISE_SETTINGS, DataGenRegistry::registerNoiseGeneratorSettings), Set.of(Multiverse.MOD_ID)));
        gen.addProvider(true, new JsonCodecProvider<>(gen.getPackOutput(), event.getExistingFileHelper(), Multiverse.MOD_ID, JsonOps.INSTANCE, PackType.SERVER_DATA, Registries.DIMENSION_TYPE.location().getPath(), DimensionType.DIRECT_CODEC, getDimensionTypes()));
    }

    private static void registerNoiseGeneratorSettings(BootstapContext<NoiseGeneratorSettings> context) {
        for (MultiverseShape shape : MultiverseShape.values()) {
            for (MultiverseType type : MultiverseType.values()) {
                context.register(shape.getNoiseSettingsKey(type), shape.createNoiseSettings(context, type));
            }
        }
    }

    private static Map<ResourceLocation, DimensionType> getDimensionTypes() {
        Map<ResourceLocation, DimensionType> types = new HashMap<>();
        for (MultiverseShape shape : MultiverseShape.values()) {
            for (MultiverseType type : MultiverseType.values()) {
                for (MultiverseTimeType time : MultiverseTimeType.values()) {
                    if (!shape.hasCeiling() || time.isNight()) {
                        for (MultiverseEffectType effect : DimensionEffectsRegistry.getEffects()) {
                            if (!effect.isNightOnly() || time.isNight()) {
                                types.put(shape.getTypeKey(type, time, effect).location(), shape.createDimensionType(type, time, effect));
                            }
                        }
                    }
                }
            }
        }
        return types;
    }

}
