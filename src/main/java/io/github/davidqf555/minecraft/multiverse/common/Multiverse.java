package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.client.ClientConfigs;
import io.github.davidqf555.minecraft.multiverse.registration.*;
import io.github.davidqf555.minecraft.multiverse.registration.custom.DimensionGeneratorTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.FluidPickerTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelGeneratorTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.*;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Multiverse.MOD_ID)
public class Multiverse {

    public static final String MOD_ID = "multiverse";

    public Multiverse() {
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        container.registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfigs.SPEC);
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
        BiomeSourceRegistry.SOURCES.register(bus);
        SurfaceRuleSourceRegistry.SOURCES.register(bus);
        EffectRegistry.EFFECTS.register(bus);
        AttachmentTypeRegistry.TYPES.register(bus);

        BiomeChunkGeneratorGeneratorTypeRegistry.TYPES.register(bus);
        BiomeDimensionGeneratorTypeRegistry.TYPES.register(bus);
        BiomeDimensionTypeGeneratorTypeRegistry.TYPES.register(bus);
        BiomeNoiseGeneratorSettingsGeneratorTypeRegistry.TYPES.register(bus);
        BiomeSourceGeneratorTypeRegistry.TYPES.register(bus);
        DimensionGeneratorTypeRegistry.TYPES.register(bus);
        FluidPickerTypeRegistry.TYPES.register(bus);
        SeaLevelGeneratorTypeRegistry.TYPES.register(bus);
        DataComponentTypeRegistry.TYPES.register(bus);
        ArmorMaterialRegistry.MATERIALS.register(bus);
        AdvancementTriggerRegistry.TRIGGERS.register(bus);
    }

}
