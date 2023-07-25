package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import net.minecraft.resources.ResourceLocation;

public class MultiverseEffectType {

    private final int weight;
    private final boolean night;
    private final ResourceLocation location;

    public MultiverseEffectType(int weight, boolean night, ResourceLocation location) {
        this.weight = weight;
        this.night = night;
        this.location = location;
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
