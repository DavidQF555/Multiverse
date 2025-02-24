package multiverse.common.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.dimension.LevelStem;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public class TargetDimensionsReader {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation loc;
    private final Set<ResourceLocation> worlds = new HashSet<>();

    public TargetDimensionsReader(ResourceLocation loc) {
        this.loc = loc;
    }

    public Set<ResourceLocation> getDimensions() {
        return worlds;
    }

    public void load(MinecraftServer server) {
        JsonElement value;
        try (Reader reader = server.getResourceManager().getResourceOrThrow(loc).openAsReader()) {
            value = GsonHelper.fromJson(GSON, reader, JsonElement.class);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        worlds.clear();
        RegistryAccess access = server.registryAccess();
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, access);
        Registry<LevelStem> registry = access.registryOrThrow(Registries.LEVEL_STEM);
        DimensionsList.CODEC.decode(ops, value).resultOrPartial(LOGGER::error).map(Pair::getFirst)
                .ifPresent(list -> {
                    switch (list.operation()) {
                        case WHITELIST:
                            for (ResourceLocation loc : list.values()) {
                                if (registry.containsKey(loc)) {
                                    worlds.add(loc);
                                } else {
                                    LOGGER.error("Could not find dimension: " + loc);
                                }
                            }
                            break;
                        case BLACKLIST:
                            for (ResourceLocation loc : registry.keySet()) {
                                if (!list.values().contains(loc)) {
                                    worlds.add(loc);
                                }
                            }
                    }
                });
        if (worlds.isEmpty()) {
            LOGGER.error("No multiverse target dimensions found");
        }
    }

}
