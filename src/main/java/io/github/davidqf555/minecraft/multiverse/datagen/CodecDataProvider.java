package io.github.davidqf555.minecraft.multiverse.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class CodecDataProvider<T> implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;
    private final ResourceKey<Registry<T>> registry;
    private final Codec<T> codec;
    private final Map<ResourceLocation, T> values;

    public CodecDataProvider(DataGenerator generator, ResourceKey<Registry<T>> registry, Codec<T> codec, Map<ResourceLocation, T> values) {
        this.generator = generator;
        this.registry = registry;
        this.codec = codec;
        this.values = values;
    }

    protected Path getPath(ResourceLocation loc) {
        ResourceLocation registry = this.registry.location();
        Path path = generator.getOutputFolder();
        String dir = registry.getNamespace().equals("minecraft") ? "" : registry.getNamespace() + "/";
        return path.resolve("data/" + loc.getNamespace() + "/" + dir + "/" + registry.getPath() + "/" + loc.getPath() + ".json");
    }

    @Override
    public void run(HashCache hashCache) throws IOException {
        DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, BuiltinRegistries.ACCESS);
        for (ResourceLocation loc : values.keySet()) {
            T val = values.get(loc);
            Path path = getPath(loc);
            JsonElement element = codec.encodeStart(ops, val).getOrThrow(false, LOGGER::error);
            DataProvider.save(GSON, hashCache, element, path);
        }
    }

    @Override
    public String getName() {
        return registry.location().toString();
    }

}
