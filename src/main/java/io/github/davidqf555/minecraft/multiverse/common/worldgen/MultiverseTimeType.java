package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import java.util.OptionalLong;

public enum MultiverseTimeType {

    DYNAMIC("dynamic", 8, OptionalLong.empty(), false),
    DAY("day", 3, OptionalLong.of(6000), false),
    NIGHT("night", 3, OptionalLong.of(18000), true),
    SUNRISE("sunrise", 1, OptionalLong.of(23500), false),
    SUNSET("sunset", 1, OptionalLong.of(12500), false);

    private final String name;
    private final int weight;
    private final OptionalLong time;
    private final boolean night;

    MultiverseTimeType(String name, int weight, OptionalLong time, boolean night) {
        this.name = name;
        this.weight = weight;
        this.time = time;
        this.night = night;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isNight() {
        return night;
    }

    public OptionalLong getTime() {
        return time;
    }

    public String getName() {
        return name;
    }


}
