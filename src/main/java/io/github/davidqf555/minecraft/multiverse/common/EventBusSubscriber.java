package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.IMultiverseNoiseGeneratorSettings;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EventBusSubscriber {

    private EventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        Registry<NoiseGeneratorSettings> settings = event.getServer().registryAccess().registryOrThrow(Registries.NOISE_SETTINGS);
        for (MultiverseShape shape : MultiverseShape.values()) {
            for (MultiverseType type : MultiverseType.values()) {
                ((IMultiverseNoiseGeneratorSettings) (Object) settings.get(shape.getNoiseSettingsKey(type))).setSettings(shape, type);
            }
        }
    }

}
