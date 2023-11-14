package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.shapes.MultiverseShape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class SeaLevelSelectorManager extends SimpleJsonResourceReloadListener {

    public static final SeaLevelSelectorManager INSTANCE = new SeaLevelSelectorManager(new GsonBuilder().create(), "worldgen/sea_level");
    private static final Logger LOGGER = LogUtils.getLogger();

    protected SeaLevelSelectorManager(Gson gson, String directory) {
        super(gson, directory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, ResourceManager manager, ProfilerFiller filler) {
        Set<MultiverseShape> shapes = EnumSet.allOf(MultiverseShape.class);
        elements.forEach((loc, element) -> {
            if (loc.getNamespace().equals(Multiverse.MOD_ID)) {
                for (MultiverseShape shape : shapes) {
                    if (loc.getPath().equals(shape.getName())) {
                        SeaLevelSelector selector = SeaLevelSelector.CODEC.get().decode(JsonOps.INSTANCE, element).getOrThrow(true, LOGGER::error).getFirst();
                        shape.setSeaLevelSelector(selector);
                        shapes.remove(shape);
                        return;
                    }
                }
                LOGGER.warn("Could not find shape: {}", loc.getPath());
            }
        });
        for (MultiverseShape shape : shapes) {
            LOGGER.error("Could not find sea level selector for {}", shape.getName());
        }
    }

}
