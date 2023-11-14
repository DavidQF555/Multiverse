package io.github.davidqf555.minecraft.multiverse.common.worldgen.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.effects.MultiverseEffect;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.EnumMap;
import java.util.Map;

public class EffectsManager {

    public static final EffectsManager INSTANCE = new EffectsManager(new ResourceLocation(Multiverse.MOD_ID, "worldgen/multiverse/effects.json"));
    public static final Codec<Pair<MultiverseEffect, Integer>> ENTRY_CODEC = Codec.mapPair(
            Codec.STRING.xmap(MultiverseEffect::byName, MultiverseEffect::getName).fieldOf("effect"),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1)
    ).codec();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<MultiverseEffect, Integer> effects = new EnumMap<>(MultiverseEffect.class);
    private final ResourceLocation loc;

    protected EffectsManager(ResourceLocation loc) {
        this.loc = loc;
    }

    public Map<MultiverseEffect, Integer> getEffects() {
        return effects;
    }

    public void load(MinecraftServer server) {
        JsonArray values;
        try (Reader reader = server.getResourceManager().getResourceOrThrow(loc).openAsReader()) {
            values = GsonHelper.fromJson(GSON, reader, JsonElement.class).getAsJsonObject().getAsJsonArray("effects");
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        effects.clear();
        for (JsonElement type : values) {
            ENTRY_CODEC.decode(ops, type).resultOrPartial(LOGGER::error).map(Pair::getFirst).ifPresent(pair -> effects.put(pair.getFirst(), effects.getOrDefault(pair.getFirst(), 0) + pair.getSecond()));
        }
        if (effects.isEmpty()) {
            throw new IllegalStateException("There cannot be 0 effects");
        }
        if (effects.values().stream().mapToInt(Integer::intValue).sum() <= 0) {
            throw new IllegalStateException("Total weight must be greater than 0");
        }
    }

}
