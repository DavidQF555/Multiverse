package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.IMultiverseNoiseGeneratorSettings;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
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

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class NoiseSettingsRegistry {

    public static final Map<ResourceLocation, NoiseSettingsEntry> SETTINGS;
    static {
        List<Pair<String, SettingsValue>> shapes = List.of(
                Pair.of("normal", new SettingsValue(true, false, NoiseSettings.create(-64, 384, new NoiseSamplingSettings(1, 1, 80, 160), new NoiseSlider(-0.078125D, 2, 8), new NoiseSlider(0.1171875, 3, 0), 1, 2, TerrainProvider.overworld(false)))),
                Pair.of("islands", new SettingsValue(false, false, NoiseSettings.create(0, 256, new NoiseSamplingSettings(2, 1, 80, 160), new NoiseSlider(-23.4375, 64, -46), new NoiseSlider(-0.234375, 7, 1), 2, 1, TerrainProvider.floatingIslands()))),
                Pair.of("roofed", new SettingsValue(true, true, NoiseSettings.create(0, 128, new NoiseSamplingSettings(1, 3, 80, 60), new NoiseSlider(0.9375, 3, 0), new NoiseSlider(2.5, 4, -1), 1, 2, TerrainProvider.nether())))
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
                        new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(), !ceiling && floor ? NoiseRouterData.overworldWithNewCaves(noise, false) : NoiseRouterData.nether(noise), SurfaceRules.state(Blocks.AIR.defaultBlockState()), 0, false, true, true, false),
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

    private record SettingsValue(boolean floor, boolean ceiling, NoiseSettings noise) {
    }

}
