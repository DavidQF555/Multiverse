package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.registration.*;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.*;
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
        BiomeModifierRegistry.SERIALIZERS.register(bus);
        CreativeModeTabRegistry.TABS.register(bus);
        SerializableFluidPickerRegistry.CODECS.register(bus);
        SeaLevelSelectorRegistry.CODECS.register(bus);
        DataComponentTypeRegistry.TYPES.register(bus);
        ArmorMaterialRegistry.MATERIALS.register(bus);
    }
}
