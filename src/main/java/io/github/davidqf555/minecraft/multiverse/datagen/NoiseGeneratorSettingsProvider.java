package io.github.davidqf555.minecraft.multiverse.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NoiseGeneratorSettingsProvider implements DataProvider {

    private final CompletableFuture<HolderLookup.Provider> lookup;
    private final PackOutput output;

    public NoiseGeneratorSettingsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        this.lookup = lookup;
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Path folder = output.getOutputFolder(PackOutput.Target.DATA_PACK);
        return lookup.thenApply(lookup -> {
            List<CompletableFuture<?>> all = new ArrayList<>();
            for (MultiverseShape shape : MultiverseShape.values()) {
                for (MultiverseType type : MultiverseType.values()) {
                    ResourceKey<NoiseGeneratorSettings> key = shape.getNoiseSettingsKey(type);
                    ResourceLocation loc = key.location();
                    NoiseGeneratorSettings settings = shape.createNoiseSettings(lookup, type);
                    Path path = folder.resolve(loc.getNamespace()).resolve(key.registry().getPath()).resolve(loc.getPath() + ".json");
                    JsonElement encoded = NoiseGeneratorSettings.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, settings).getOrThrow(false, (msg) -> {
                        LOGGER.error("Failed to encode {}: {}", path, msg);
                    });
                    all.add(DataProvider.saveStable(cachedOutput, encoded, path));
                }
            }
            return CompletableFuture.allOf(all.toArray(CompletableFuture[]::new)).join();
        });
    }

    @Override
    public String getName() {
        return "Noise Generator Settings";
    }

}
