package io.github.davidqf555.minecraft.multiverse.common.worldgen.shapes;

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

public class ShapesManager {

    public static final ShapesManager INSTANCE = new ShapesManager(new ResourceLocation(Multiverse.MOD_ID, "shapes.json"));
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Set<MultiverseShape> shapes = new HashSet<>();
    private final ResourceLocation loc;

    protected ShapesManager(ResourceLocation loc) {
        this.loc = loc;
    }

    public Set<MultiverseShape> getShapes() {
        return shapes;
    }

    public void load(MinecraftServer server) {
        Reader reader;
        try {
            reader = server.getResourceManager().getResourceOrThrow(loc).openAsReader();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        JsonArray values = GsonHelper.fromJson(GSON, reader, JsonElement.class).getAsJsonObject().getAsJsonArray("shapes");
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        shapes.clear();
        for (JsonElement type : values) {
            MultiverseShape.CODEC.decode(ops, type).resultOrPartial(LOGGER::error).map(Pair::getFirst).ifPresent(shapes::add);
        }
        if (shapes.isEmpty()) {
            throw new IllegalStateException("There cannot be 0 shapes");
        }
        if (shapes.stream().mapToInt(MultiverseShape::getWeight).sum() <= 0) {
            throw new IllegalStateException("Total weight must be greater than 0");
        }
    }

}
