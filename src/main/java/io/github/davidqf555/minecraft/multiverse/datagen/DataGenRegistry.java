package io.github.davidqf555.minecraft.multiverse.datagen;

import com.mojang.serialization.JsonOps;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseEffectType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseTimeType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenRegistry {

    private DataGenRegistry() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) throws ExecutionException, InterruptedException {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(true, new JsonCodecProvider<>(gen.getPackOutput(), event.getExistingFileHelper(), Multiverse.MOD_ID, JsonOps.INSTANCE, PackType.SERVER_DATA, Registries.NOISE_SETTINGS.location().getPath(), NoiseGeneratorSettings.DIRECT_CODEC, getNoiseGeneratorSettings(event.getLookupProvider().get())));
        gen.addProvider(true, new JsonCodecProvider<>(gen.getPackOutput(), event.getExistingFileHelper(), Multiverse.MOD_ID, JsonOps.INSTANCE, PackType.SERVER_DATA, Registries.DIMENSION_TYPE.location().getPath(), DimensionType.DIRECT_CODEC, getDimensionTypes()));
    }

    private static Map<ResourceLocation, NoiseGeneratorSettings> getNoiseGeneratorSettings(HolderLookup.Provider provider) {
        Map<ResourceLocation, NoiseGeneratorSettings> settings = new HashMap<>();
        for (MultiverseShape shape : MultiverseShape.values()) {
            for (MultiverseType type : MultiverseType.values()) {
                settings.put(shape.getNoiseSettingsKey(type).location(), shape.createNoiseSettings(provider, type));
            }
        }
        return settings;
    }

    private static Map<ResourceLocation, DimensionType> getDimensionTypes() {
        Map<ResourceLocation, DimensionType> types = new HashMap<>();
        for (MultiverseShape shape : MultiverseShape.values()) {
            for (MultiverseType type : MultiverseType.values()) {
                for (MultiverseTimeType time : MultiverseTimeType.values()) {
                    if (!shape.hasCeiling() || time.isNight()) {
                        for (MultiverseEffectType effect : MultiverseEffectType.values()) {
                            types.put(shape.getTypeKey(type, time, effect).location(), shape.createDimensionType(type, time, effect));
                        }
                    }
                }
            }
        }
        return types;
    }

}
