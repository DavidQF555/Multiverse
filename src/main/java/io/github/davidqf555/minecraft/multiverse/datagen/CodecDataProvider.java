package io.github.davidqf555.minecraft.multiverse.datagen;

import com.google.gson.Gson;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class CodecDataProvider<T> implements DataProvider {

    private static final Gson GSON = new Gson();
    private final Map<ResourceLocation, T> values;
    private final DataGenerator gen;
    private final Codec<T> codec;

    public CodecDataProvider(DataGenerator gen, Codec<T> codec) {
        this.gen = gen;
        this.codec = codec;
        values = new HashMap<>();
    }

    protected abstract void init();

    protected void add(ResourceLocation id, T value) {
        values.put(id, value);
    }

    @Override
    public void run(HashCache cache) {
        init();
        values.forEach((id, value) -> {
            try {
                DataProvider.save(GSON, cache, codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow(false, string -> {
                }), gen.getOutputFolder().resolve(getPath(id)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected abstract String getPath(ResourceLocation id);

}