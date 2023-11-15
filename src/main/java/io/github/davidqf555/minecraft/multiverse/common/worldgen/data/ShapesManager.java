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
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
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

public class ShapesManager {

    public static final ShapesManager INSTANCE = new ShapesManager(new ResourceLocation(Multiverse.MOD_ID, "worldgen/multiverse/shapes.json"));
    public static final Codec<Pair<MultiverseShape, Integer>> ENTRY_CODEC = Codec.mapPair(
            Codec.STRING.xmap(MultiverseShape::byName, MultiverseShape::getName).fieldOf("shape"),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1)
    ).codec();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<MultiverseShape, Integer> shapes = new EnumMap<>(MultiverseShape.class);
    private final ResourceLocation loc;

    protected ShapesManager(ResourceLocation loc) {
        this.loc = loc;
    }

    public Map<MultiverseShape, Integer> getShapes() {
        return shapes;
    }

    public void load(MinecraftServer server) {
        JsonArray values;
        try (Reader reader = server.getResourceManager().getResourceOrThrow(loc).openAsReader()) {
            values = GsonHelper.fromJson(GSON, reader, JsonElement.class).getAsJsonObject().getAsJsonArray("shapes");
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        shapes.clear();
        for (JsonElement type : values) {
            ENTRY_CODEC.decode(ops, type).resultOrPartial(LOGGER::error).map(Pair::getFirst).ifPresent(pair -> shapes.put(pair.getFirst(), shapes.getOrDefault(pair.getFirst(), 0) + pair.getSecond()));
        }
        if (shapes.isEmpty()) {
            throw new IllegalStateException("There cannot be 0 shapes");
        }
        if (shapes.values().stream().mapToInt(Integer::intValue).sum() <= 0) {
            throw new IllegalStateException("Total weight must be greater than 0");
        }
    }

}
