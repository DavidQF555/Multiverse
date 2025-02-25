package multiverse.common.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import multiverse.common.util.MultiverseConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public final class DimensionHelper {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    private DimensionHelper() {
    }

    public static long resourceLocationToSeed(ResourceLocation dir, long base, long factor) {
        String loc = dir.getNamespace();
        String path = dir.getPath();
        int i = 0;
        int j = 0;
        while (i < loc.length() || j < path.length()) {
            char c;
            if (i >= loc.length()) {
                c = path.charAt(j++);
            } else if (j >= path.length()) {
                c = loc.charAt(i++);
            } else if ((i + j) % 2 == 0) {
                c = path.charAt(j++);
            } else {
                c = loc.charAt(i++);
            }
            base += factor * c * (i + j);
        }
        return base;
    }

    public static void loadTargetDimensions(MinecraftServer server, ResourceLocation loc) throws IOException {
        Set<ResourceKey<Level>> worlds = new HashSet<>();
        JsonElement value;
        try (Reader reader = server.getResourceManager().getResourceOrThrow(loc).openAsReader()) {
            value = GsonHelper.fromJson(GSON, reader, JsonElement.class);
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        Registry<LevelStem> registry = server.getWorldData().worldGenSettings().dimensions();
        DimensionsList.CODEC.decode(ops, value).resultOrPartial(LOGGER::error).map(Pair::getFirst)
                .ifPresent(list -> {
                    switch (list.operation()) {
                        case WHITELIST:
                            for (ResourceLocation dir : list.values()) {
                                if (registry.containsKey(dir)) {
                                    worlds.add(ResourceKey.create(Registry.DIMENSION_REGISTRY, dir));
                                } else {
                                    LOGGER.error("Could not find dimension: " + loc);
                                }
                            }
                            break;
                        case BLACKLIST:
                            for (ResourceLocation dir : registry.keySet()) {
                                if (!list.values().contains(dir)) {
                                    worlds.add(ResourceKey.create(Registry.DIMENSION_REGISTRY, dir));
                                }
                            }
                    }
                });
        if (worlds.isEmpty()) {
            LOGGER.error("No multiverse target dimensions found");
        }
        MultiverseConfig.setTargetDimensions(worlds);
    }

}
