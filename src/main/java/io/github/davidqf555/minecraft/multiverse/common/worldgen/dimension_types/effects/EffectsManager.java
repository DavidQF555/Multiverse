package io.github.davidqf555.minecraft.multiverse.common.worldgen.dimension_types.effects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public class EffectsManager {

    public static final EffectsManager INSTANCE = new EffectsManager(new ResourceLocation(Multiverse.MOD_ID, "effects.json"));
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Set<MultiverseEffect> effects = new HashSet<>();
    private final ResourceLocation loc;

    protected EffectsManager(ResourceLocation loc) {
        this.loc = loc;
    }

    public Set<MultiverseEffect> getEffects() {
        return effects;
    }

    public void load(MinecraftServer server) {
        Reader reader;

        try {
            reader = server.getResourceManager().getResourceOrThrow(loc).openAsReader();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        JsonArray values = GsonHelper.fromJson(GSON, reader, JsonElement.class).getAsJsonObject().getAsJsonArray("effects");
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        effects.clear();
        for (JsonElement type : values) {
            MultiverseEffect.CODEC.decode(ops, type).resultOrPartial(LOGGER::error).map(Pair::getFirst).ifPresent(effects::add);
        }
        if (effects.isEmpty()) {
            throw new IllegalStateException("There cannot be 0 effects");
        }
        if (effects.stream().mapToInt(MultiverseEffect::getWeight).sum() <= 0) {
            throw new IllegalStateException("Total weight must be greater than 0");
        }
    }

}
