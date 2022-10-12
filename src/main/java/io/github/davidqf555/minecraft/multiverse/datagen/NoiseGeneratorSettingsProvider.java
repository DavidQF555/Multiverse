package io.github.davidqf555.minecraft.multiverse.datagen;

import io.github.davidqf555.minecraft.multiverse.common.registration.worldgen.NoiseSettingsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class NoiseGeneratorSettingsProvider extends CodecDataProvider<NoiseGeneratorSettings> {

    public NoiseGeneratorSettingsProvider(DataGenerator gen) {
        super(gen, NoiseGeneratorSettings.DIRECT_CODEC);
    }

    @Override
    protected void init() {
        NoiseSettingsRegistry.ALL.forEach((key, value) -> {
            add(key.location(), value);
        });
    }

    @Override
    protected String getPath(ResourceLocation id) {
        return "data/" + id.getNamespace() + "/worldgen/noise_settings/" + id.getPath() + ".json";
    }

    @Override
    public String getName() {
        return "Noise Generator Settings";
    }
}
