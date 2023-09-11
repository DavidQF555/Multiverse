package io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BiomeTypesManager extends SimplePreparableReloadListener<JsonElement> {

    public static final BiomeTypesManager INSTANCE = new BiomeTypesManager(new ResourceLocation(Multiverse.MOD_ID, "biome_types.json"));
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Set<BiomeType> biomes = new HashSet<>();
    private final ResourceLocation loc;

    public BiomeTypesManager(ResourceLocation loc) {
        this.loc = loc;
    }

    @Override
    protected JsonElement prepare(ResourceManager manager, ProfilerFiller filler) {
        Resource resource;
        try {
            resource = manager.getResourceOrThrow(loc);
            return GsonHelper.fromJson(GSON, resource.openAsReader(), JsonElement.class);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    protected void apply(JsonElement element, ResourceManager manager, ProfilerFiller filler) {
        biomes.clear();
        JsonArray values = element.getAsJsonObject().getAsJsonArray("types");
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        for (JsonElement type : values) {
            BiomeType.CODEC.decode(ops, type).resultOrPartial(LOGGER::error).map(Pair::getFirst).ifPresent(biomes::add);
        }
        if (biomes.isEmpty()) {
            throw new IllegalStateException("There cannot be 0 biome types");
        }
        if (biomes.stream().mapToInt(BiomeType::getWeight).sum() <= 0) {
            throw new IllegalStateException("Total weight must be greater than 0");
        }
    }

    public Set<BiomeType> getBiomeTypes() {
        return biomes;
    }

}
