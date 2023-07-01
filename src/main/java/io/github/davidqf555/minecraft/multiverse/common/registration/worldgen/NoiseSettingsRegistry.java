package io.github.davidqf555.minecraft.multiverse.common.registration.worldgen;

import com.google.common.collect.ImmutableMap;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class NoiseSettingsRegistry {

    public static final Map<ResourceKey<NoiseGeneratorSettings>, NoiseGeneratorSettings> ALL;
    public static final ResourceKey<NoiseGeneratorSettings> TOP = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "top"));
    public static final ResourceKey<NoiseGeneratorSettings> BOTTOM = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "bottom"));
    public static final ResourceKey<NoiseGeneratorSettings> NONE = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "none"));
    public static final ResourceKey<NoiseGeneratorSettings> BOTH = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, "both"));

    static {
        ImmutableMap.Builder<ResourceKey<NoiseGeneratorSettings>, NoiseGeneratorSettings> builder = ImmutableMap.builder();
        builder.put(TOP, createDefaultSettings(false, true, NoiseSettings.create(0, 128, new NoiseSamplingSettings(1, 1, 80, 30), new NoiseSlider(120, 3, 0), new NoiseSlider(-30, 7, 1), 1, 2, TerrainProvider.nether()), 0));
        builder.put(BOTTOM, createDefaultSettings(true, false, NoiseSettings.create(0, 256, new NoiseSamplingSettings(1, 2, 80, 160), new NoiseSlider(-10, 3, 0), new NoiseSlider(-30, 0, 0), 1, 2, TerrainProvider.overworld(false)), 63));
        builder.put(NONE, createDefaultSettings(false, false, NoiseSettings.create(0, 128, new NoiseSamplingSettings(2, 1, 80, 160), new NoiseSlider(-3000, 64, -46), new NoiseSlider(-30, 7, 1), 2, 1, TerrainProvider.end()), 0));
        builder.put(BOTH, createDefaultSettings(true, true, NoiseSettings.create(0, 128, new NoiseSamplingSettings(1, 3, 80, 60), new NoiseSlider(120, 3, 0), new NoiseSlider(320, 4, -1), 1, 2, TerrainProvider.nether()), 32));
        ALL = builder.build();
    }

    private NoiseSettingsRegistry() {
    }

    private static NoiseGeneratorSettings createDefaultSettings(boolean floor, boolean ceiling, NoiseSettings noise, int sea) {
        return new NoiseGeneratorSettings(noise, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), NoiseRouterData.overworldWithNewCaves(noise, false), getDefaultSurfaceRule(floor, ceiling), sea, false, true, true, false);
    }

    private static SurfaceRules.RuleSource getDefaultSurfaceRule(boolean floor, boolean ceiling) {
        return SurfaceRuleData.overworldLike(true, ceiling, floor);
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ALL.forEach((key, settings) -> {
                Registry.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, key.location(), settings);
            });
        });
    }

}
