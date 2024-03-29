package io.github.davidqf555.minecraft.multiverse.common.worldgen.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.BiomeType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.VanillaMultiverseBiomes;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public class BiomesManager {

    public static final BiomesManager INSTANCE = new BiomesManager(new ResourceLocation(Multiverse.MOD_ID, "worldgen/multiverse/biomes.json"));
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Set<BiomeType> types = new HashSet<>();
    private final ResourceLocation loc;
    private MultiverseBiomes biomes = VanillaMultiverseBiomes.INSTANCE;

    protected BiomesManager(ResourceLocation loc) {
        this.loc = loc;
    }

    public Set<BiomeType> getBiomeTypes() {
        return types;
    }

    public MultiverseBiomes getBiomes() {
        return biomes;
    }

    public void setBiomes(MultiverseBiomes biomes) {
        this.biomes = biomes;
    }

    public void load(MinecraftServer server) {
        JsonArray values;
        try (Reader reader = server.getResourceManager().getResourceOrThrow(loc).openAsReader()) {
            values = GsonHelper.fromJson(GSON, reader, JsonElement.class).getAsJsonObject().getAsJsonArray("types");
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        types.clear();
        for (JsonElement type : values) {
            BiomeType.CODEC.decode(ops, type).resultOrPartial(LOGGER::error).map(Pair::getFirst).ifPresent(types::add);
        }
        if (types.isEmpty()) {
            throw new IllegalStateException("There cannot be 0 biome types");
        }
        if (types.stream().mapToInt(BiomeType::getWeight).sum() <= 0) {
            throw new IllegalStateException("Total weight must be greater than 0");
        }
    }

}
