package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class NoiseSettingsRegistry {

    public static final Map<ResourceLocation, NoiseSettingsEntry> SETTINGS;
    static {
        List<Pair<String, SettingsValue>> shapes = List.of(
                Pair.of("normal", new SettingsValue(true, false, true, NoiseSettings.create(-64, 384, new NoiseSamplingSettings(1, 1, 80, 160), new NoiseSlider(-0.078125D, 2, 8), new NoiseSlider(0.1171875, 3, 0), 1, 2, TerrainProvider.overworld(false)), noise -> NoiseRouterData.overworldWithNewCaves(noise, false))),
                Pair.of("amplified", new SettingsValue(true, false, true, NoiseSettings.create(-64, 384, new NoiseSamplingSettings(1, 1, 80, 160), new NoiseSlider(-0.078125, 2, 0), new NoiseSlider(0.4, 3, 0), 1, 2, TerrainProvider.overworld(true)), noise -> NoiseRouterData.overworldWithNewCaves(noise, false))),
                Pair.of("mountains", new SettingsValue(true, false, true, NoiseSettings.create(-64, 384, new NoiseSamplingSettings(1, 1, 80, 160), new NoiseSlider(-0.078125D, 2, 0), new NoiseSlider(0.4, 3, 0), 1, 2, MultiverseTerrainShapers.mountains()), noise -> NoiseRouterData.overworldWithNewCaves(noise, false))),

                Pair.of("roofed", new SettingsValue(true, true, false, NoiseSettings.create(0, 128, new NoiseSamplingSettings(1, 3, 80, 60), new NoiseSlider(0.9375, 3, 0), new NoiseSlider(2.5, 4, -1), 1, 2, TerrainProvider.nether()), NoiseRouterData::nether)),
                Pair.of("underwater", new SettingsValue(true, true, false, NoiseSettings.create(0, 128, new NoiseSamplingSettings(1, 3, 80, 60), new NoiseSlider(0.9375, 3, 0), new NoiseSlider(2.5, 4, -1), 1, 2, TerrainProvider.nether()), NoiseRouterData::nether)),

                Pair.of("islands", new SettingsValue(false, false, false, NoiseSettings.create(0, 256, new NoiseSamplingSettings(2, 1, 80, 160), new NoiseSlider(-23.4375, 64, -46), new NoiseSlider(-0.234375, 7, 1), 2, 1, TerrainProvider.floatingIslands()), NoiseRouterData::overworldWithoutCaves)),
                Pair.of("noodles", new SettingsValue(false, false, false, NoiseSettings.create(0, 256, new NoiseSamplingSettings(2, 1, 80, 160), new NoiseSlider(-23.4375, 64, -46), new NoiseSlider(-0.234375, 7, 1), 2, 1, TerrainProvider.floatingIslands()), noise -> MultiverseNoiseRouters.noodles(noise, 0.1, 0.3))),
                Pair.of("blobs", new SettingsValue(false, false, false, NoiseSettings.create(0, 256, new NoiseSamplingSettings(2, 1, 80, 160), new NoiseSlider(-23.4375, 64, -46), new NoiseSlider(-0.234375, 7, 1), 2, 1, TerrainProvider.floatingIslands()), MultiverseNoiseRouters::blobs))
        );
        ImmutableMap.Builder<ResourceLocation, NoiseSettingsEntry> builder = ImmutableMap.builder();
        for (Pair<String, SettingsValue> pair : shapes) {
            SettingsValue val = pair.getSecond();
            boolean floor = val.floor();
            boolean ceiling = val.ceiling();
            NoiseSettings noise = val.noise();
            NoiseRouterWithOnlyNoises router = val.router().apply(noise);
            for (MultiverseType type : MultiverseType.values()) {
                ResourceLocation loc = new ResourceLocation(Multiverse.MOD_ID, pair.getFirst() + "/" + type.getName());
                builder.put(loc, new NoiseSettingsEntry(
                        new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(),
                                router,
                                SurfaceRules.state(Blocks.AIR.defaultBlockState()),
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

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        Registry<NoiseGeneratorSettings> registry = event.getServer().registryAccess().registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        SETTINGS.forEach((loc, val) -> registry.getOptional(loc).ifPresent(settings -> ((IMultiverseNoiseGeneratorSettings) (Object) settings).setSettings(val.floor(), val.ceiling(), val.type())));
    }

    private NoiseSettingsRegistry() {
    }

    public record NoiseSettingsEntry(NoiseGeneratorSettings settings, MultiverseType type, boolean floor,
                                     boolean ceiling) {
    }

    private record SettingsValue(boolean floor, boolean ceiling, boolean aquifers, NoiseSettings noise,
                                 Function<NoiseSettings, NoiseRouterWithOnlyNoises> router) {
    }

}