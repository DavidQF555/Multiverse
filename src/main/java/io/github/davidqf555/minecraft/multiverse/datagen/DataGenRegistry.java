package io.github.davidqf555.minecraft.multiverse.datagen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenRegistry {

    private DataGenRegistry() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        if (event.includeServer()) {
            DataGenerator gen = event.getGenerator();
            gen.addProvider(true, new DatapackBuiltinEntriesProvider(gen.getPackOutput(), event.getLookupProvider(),
                            new RegistrySetBuilder().add(Registries.NOISE_SETTINGS, context -> {
                                HolderGetter<DensityFunction> density = context.lookup(Registries.DENSITY_FUNCTION);
                                HolderGetter<NormalNoise.NoiseParameters> parameters = context.lookup(Registries.NOISE);
                                NoiseSettingsRegistry.SETTINGS.forEach((loc, val) -> context.register(ResourceKey.create(Registries.NOISE_SETTINGS, loc), val.settings().apply(density, parameters)));
                            }).add(Registries.DIMENSION_TYPE, context -> DimensionTypeRegistry.TYPES.forEach((loc, type) -> context.register(ResourceKey.create(Registries.DIMENSION_TYPE, loc), type))),
                            Set.of(Multiverse.MOD_ID)
                    )
            );
        }
    }

}
