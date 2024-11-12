package io.github.davidqf555.minecraft.multiverse.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
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

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenRegistry {

    private DataGenRegistry() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        if (event.includeServer()) {
            DataGenerator gen = event.getGenerator();
            Map<ResourceLocation, NoiseGeneratorSettings> noise = new HashMap<>();
            NoiseSettingsRegistry.SETTINGS.forEach((loc, val) -> noise.put(loc, val.settings().apply(BuiltinRegistries.DENSITY_FUNCTION)));
            DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, BuiltinRegistries.ACCESS);
            gen.addProvider(true, new JsonCodecProvider<>(gen, event.getExistingFileHelper(), Multiverse.MOD_ID, ops, PackType.SERVER_DATA, Registry.NOISE_GENERATOR_SETTINGS_REGISTRY.location().getPath(), NoiseGeneratorSettings.DIRECT_CODEC, noise));
            gen.addProvider(true, new JsonCodecProvider<>(gen, event.getExistingFileHelper(), Multiverse.MOD_ID, ops, PackType.SERVER_DATA, Registry.DIMENSION_TYPE_REGISTRY.location().getPath(), DimensionType.DIRECT_CODEC, DimensionTypeRegistry.TYPES));
        }
    }

}
