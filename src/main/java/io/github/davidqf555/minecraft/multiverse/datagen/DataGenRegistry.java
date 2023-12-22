package io.github.davidqf555.minecraft.multiverse.datagen;

import com.mojang.serialization.JsonOps;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseTime;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.effects.MultiverseEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumSet;
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
        gen.addProvider(true, new NoiseGeneratorSettingsProvider(gen.getPackOutput(), event.getLookupProvider()));
        gen.addProvider(true, new JsonCodecProvider<>(gen.getPackOutput(), event.getExistingFileHelper(), Multiverse.MOD_ID, JsonOps.INSTANCE, PackType.SERVER_DATA, Registries.DIMENSION_TYPE.location().getPath(), DimensionType.DIRECT_CODEC, getDimensionTypes()));
    }

    private static Map<ResourceLocation, DimensionType> getDimensionTypes() {
        Map<ResourceLocation, DimensionType> types = new HashMap<>();
        for (MultiverseShape shape : MultiverseShape.values()) {
            Set<MultiverseTime> times = shape.getFixedTime().map(Set::of).orElseGet(() -> EnumSet.allOf(MultiverseTime.class));
            for (MultiverseType type : MultiverseType.values()) {
                for (MultiverseTime time : times) {
                    for (MultiverseEffect effect : MultiverseEffect.values()) {
                        types.put(shape.getTypeKey(type, time, effect).location(), shape.createDimensionType(type, time, effect));
                    }
                }
            }
        }
        return types;
    }

}
