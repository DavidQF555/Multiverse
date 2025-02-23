package multiverse.common.world.worldgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.dimension.LevelStem;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class TargetDimensionsReader {

    private static final Codec<List<ResourceKey<LevelStem>>> CODEC = ResourceKey.codec(Registry.LEVEL_STEM_REGISTRY).listOf().fieldOf("dimensions").codec();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation loc;
    private final List<ResourceKey<LevelStem>> worlds = new ArrayList<>();

    public TargetDimensionsReader(ResourceLocation loc) {
        this.loc = loc;
    }

    public List<ResourceKey<LevelStem>> getDimensions() {
        return worlds;
    }

    public void load(MinecraftServer server) {
        JsonElement value;
        try (Reader reader = server.getResourceManager().getResourceOrThrow(loc).openAsReader()) {
            value = GsonHelper.fromJson(GSON, reader, JsonElement.class);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        worlds.clear();
        Registry<LevelStem> registry = server.getWorldData().worldGenSettings().dimensions();
        CODEC.decode(ops, value).resultOrPartial(LOGGER::error).map(Pair::getFirst)
                .ifPresent(list -> {
                    for (ResourceKey<LevelStem> key : list) {
                        if (registry.containsKey(key)) {
                            worlds.add(key);
                        } else {
                            LOGGER.error("Could not find dimension: " + key.location());
                        }
                    }
                });
    }

}
