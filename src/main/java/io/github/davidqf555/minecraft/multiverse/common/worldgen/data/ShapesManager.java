package io.github.davidqf555.minecraft.multiverse.common.worldgen.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ShapesManager {

    public static final ShapesManager INSTANCE = new ShapesManager(new ResourceLocation(Multiverse.MOD_ID, "shapes.json"));
    public static final Codec<List<Entry>> ENTRY_CODEC = RecordCodecBuilder.<Entry>create(inst -> inst.group(
            MultiverseShape.CODEC.fieldOf("shape").forGetter(Entry::shape),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(Entry::weight)
    ).apply(inst, Entry::new)).listOf().fieldOf("shapes").codec();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<Entry> shapes = new ArrayList<>();
    private final ResourceLocation loc;

    protected ShapesManager(ResourceLocation loc) {
        this.loc = loc;
    }

    public List<Entry> getShapes() {
        return shapes;
    }

    public void load(MinecraftServer server) {
        JsonElement value;
        try (Reader reader = server.getResourceManager().getResourceOrThrow(loc).openAsReader()) {
            value = GsonHelper.fromJson(GSON, reader, JsonElement.class);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        shapes.clear();
        ENTRY_CODEC.decode(ops, value).resultOrPartial(LOGGER::error).map(Pair::getFirst)
                .ifPresent(shapes::addAll);
        if (shapes.isEmpty()) {
            throw new IllegalStateException("There cannot be 0 shapes");
        }
        if (shapes.stream().mapToInt(Entry::weight).sum() <= 0) {
            throw new IllegalStateException("Total weight must be greater than 0");
        }
    }

    public record Entry(Holder<MultiverseShape> shape, int weight) {
    }

}