package io.github.davidqf555.minecraft.multiverse.common.integration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.util.ConfigHelper;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.LazyMultiverseBiomes;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class IntegrationRegistry {

    private IntegrationRegistry() {
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        if (ModList.get().isLoaded("terrablender")) {
            ConfigHelper.setBiomesManager(new LazyMultiverseBiomes(() -> new TerraBlenderBiomes(event.getServer().registryAccess().registryOrThrow(Registries.BIOME))));
        }
    }

}
