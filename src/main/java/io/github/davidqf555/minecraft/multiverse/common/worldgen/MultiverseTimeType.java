package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import java.util.OptionalLong;

public enum MultiverseTimeType {

    DYNAMIC("dynamic", OptionalLong.empty(), false),
    DAY("day", OptionalLong.of(6000), false),
    NIGHT("night", OptionalLong.of(18000), true),
    SUNRISE("sunrise", OptionalLong.of(23500), false),
    SUNSET("sunset", OptionalLong.of(12500), false);

    private final String name;
    private final OptionalLong time;
    private final boolean night;

    MultiverseTimeType(String name, OptionalLong time, boolean night) {
        this.name = name;
        this.time = time;
        this.night = night;
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
