package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MultiverseEffectType {

    private static final List<MultiverseEffectType> TYPES = new ArrayList<>();
    private final int weight;
    private final boolean night;
    private final ResourceLocation location;

    public MultiverseEffectType(int weight, boolean night, ResourceLocation location) {
        this.weight = weight;
        this.night = night;
        this.location = location;
    }

    public static void register(MultiverseEffectType type) {
        TYPES.add(type);
    }

    public static List<MultiverseEffectType> getTypes() {
        return TYPES;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isNightOnly() {
        return night;
    }

    public ResourceLocation getLocation() {
        return location;
    }

}
