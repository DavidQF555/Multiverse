package multiverse.datagen;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.LazyMultiverseSurfaceRuleSource;
import multiverse.common.world.worldgen.MultiverseType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class NoiseSettingsRegistry {

    public static final Map<ResourceLocation, NoiseSettingsEntry> SETTINGS;

    static {
        List<Pair<String, SettingsValue>> shapes = List.of(
                Pair.of("normal", new SettingsValue(true, false, true, NoiseSettings.create(-64, 384, 1, 2), density -> NoiseRouterData.overworld(density, false, false))),
                Pair.of("amplified", new SettingsValue(true, false, true, NoiseSettings.create(-64, 384, 1, 2), density -> NoiseRouterData.overworld(density, false, true))),
                Pair.of("flooded/normal", new SettingsValue(true, false, false, NoiseSettings.create(-64, 384, 1, 2), density -> NoiseRouterData.overworld(density, false, false))),
                Pair.of("flooded/amplified", new SettingsValue(true, false, false, NoiseSettings.create(-64, 384, 1, 2), density -> NoiseRouterData.overworld(density, false, true))),

                Pair.of("roofed", new SettingsValue(true, true, false, NoiseSettings.create(0, 128, 1, 2), NoiseRouterData::nether)),

                Pair.of("islands", new SettingsValue(false, false, false, NoiseSettings.create(0, 256, 2, 1), NoiseRouterData::floatingIslands)),
                Pair.of("noodles", new SettingsValue(false, false, false, NoiseSettings.create(0, 256, 2, 1), density -> MultiverseNoiseRouters.noodles(density, 0, 256))),
                Pair.of("blobs", new SettingsValue(false, false, false, NoiseSettings.create(0, 256, 2, 1), density -> MultiverseNoiseRouters.blobs(density, 0, 256)))
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
                        density -> new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(),
                                val.router().apply(density),
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
            Function<Registry<DensityFunction>, NoiseGeneratorSettings> settings,
            MultiverseType type, boolean floor,
            boolean ceiling) {
    }

    private record SettingsValue(boolean floor, boolean ceiling, boolean aquifers, NoiseSettings noise,
                                 Function<Registry<DensityFunction>, NoiseRouter> router) {
    }

}