package io.github.davidqf555.minecraft.multiverse.datagen;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.LazyMultiverseSurfaceRuleSource;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public final class NoiseSettingsRegistry {

    public static final Map<ResourceLocation, NoiseSettingsEntry> SETTINGS;

    static {
        List<Pair<String, SettingsValue>> shapes = List.of(
                Pair.of("normal", new SettingsValue(true, false, true, NoiseSettings.create(-64, 384, 1, 2), (density, noise) -> NoiseRouterData.overworld(density, noise, false, false))),
                Pair.of("amplified", new SettingsValue(true, false, true, NoiseSettings.create(-64, 384, 1, 2), (density, noise) -> NoiseRouterData.overworld(density, noise, false, true))),
                Pair.of("flooded/normal", new SettingsValue(true, false, false, NoiseSettings.create(-64, 384, 1, 2), (density, noise) -> NoiseRouterData.overworld(density, noise, false, false))),
                Pair.of("flooded/amplified", new SettingsValue(true, false, false, NoiseSettings.create(-64, 384, 1, 2), (density, noise) -> NoiseRouterData.overworld(density, noise, false, true))),

                Pair.of("roofed", new SettingsValue(true, true, false, NoiseSettings.create(0, 128, 1, 2), NoiseRouterData::nether)),

                Pair.of("islands", new SettingsValue(false, false, false, NoiseSettings.create(0, 256, 2, 1), NoiseRouterData::floatingIslands)),
                Pair.of("noodles", new SettingsValue(false, false, false, NoiseSettings.create(0, 256, 2, 1), (density, noise) -> MultiverseNoiseRouters.noodles(density, noise, 0, 256))),
                Pair.of("blobs", new SettingsValue(false, false, false, NoiseSettings.create(0, 256, 2, 1), (density, noise) -> MultiverseNoiseRouters.blobs(density, noise, 0, 256)))
        );
        ImmutableMap.Builder<ResourceLocation, NoiseSettingsEntry> builder = ImmutableMap.builder();
        for (Pair<String, SettingsValue> pair : shapes) {
            SettingsValue val = pair.getSecond();
            boolean floor = val.floor();
            boolean ceiling = val.ceiling();
            NoiseSettings noise = val.noise();
            for (MultiverseType type : MultiverseType.values()) {
                ResourceLocation loc = new ResourceLocation(Multiverse.MOD_ID, pair.getFirst() + "/" + type.getName());
                builder.put(loc, new NoiseSettingsEntry(
                        (density, hol) -> new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(),
                                val.router().apply(density, hol),
                                new LazyMultiverseSurfaceRuleSource(floor, ceiling, type),
                                List.of(),
                                0,
                                false,
                                val.aquifers(),
                                true,
                                false
                        ),
                        type,
                        floor,
                        ceiling
                ));
            }
        }
        SETTINGS = builder.build();
    }

    private NoiseSettingsRegistry() {
    }

    public record NoiseSettingsEntry(
            BiFunction<HolderGetter<DensityFunction>, HolderGetter<NormalNoise.NoiseParameters>, NoiseGeneratorSettings> settings,
            MultiverseType type, boolean floor,
            boolean ceiling) {
    }

    private record SettingsValue(boolean floor, boolean ceiling, boolean aquifers, NoiseSettings noise,
                                 BiFunction<HolderGetter<DensityFunction>, HolderGetter<NormalNoise.NoiseParameters>, NoiseRouter> router) {
    }

}