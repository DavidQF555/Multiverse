package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.registration.*;
import io.github.davidqf555.minecraft.multiverse.registration.custom.DimensionProviderTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.FluidPickerTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelProviderTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.*;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.ChunkGeneratorRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.PlacementRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod("multiverse")
public class Multiverse {

    public static final String MOD_ID = "multiverse";

    public Multiverse() {
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        container.registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC);
        addRegistries(container.getEventBus());
    }

    private void addRegistries(IEventBus bus) {
        BlockRegistry.BLOCKS.register(bus);
        EntityRegistry.TYPES.register(bus);
        FeatureRegistry.FEATURES.register(bus);
        ItemRegistry.ITEMS.register(bus);
        POIRegistry.TYPES.register(bus);
        TileEntityRegistry.TYPES.register(bus);
        ParticleTypeRegistry.TYPES.register(bus);
        PlacementRegistry.TYPES.register(bus);
        ChunkGeneratorRegistry.GENERATORS.register(bus);
        CreativeModeTabRegistry.TABS.register(bus);
        BiomeChunkGeneratorProviderTypeRegistry.TYPES.register(bus);
        BiomeDimensionProviderTypeRegistry.TYPES.register(bus);
        BiomeDimensionTypeProviderTypeRegistry.TYPES.register(bus);
        BiomeNoiseGeneratorSettingsProviderTypeRegistry.TYPES.register(bus);
        BiomeSourceProviderTypeRegistry.TYPES.register(bus);
        DimensionProviderTypeRegistry.TYPES.register(bus);
        FluidPickerTypeRegistry.TYPES.register(bus);
        SeaLevelProviderTypeRegistry.TYPES.register(bus);
        DataComponentTypeRegistry.TYPES.register(bus);
        AdvancementTriggerRegistry.TRIGGERS.register(bus);
    }

}
