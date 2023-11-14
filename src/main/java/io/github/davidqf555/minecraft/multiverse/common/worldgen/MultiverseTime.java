package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import javax.annotation.Nullable;
import java.util.OptionalLong;

public enum MultiverseTime {

    DYNAMIC("dynamic", OptionalLong.empty()),
    DAY("day", OptionalLong.of(6000)),
    NIGHT("night", OptionalLong.of(18000)),
    SUNRISE("sunrise", OptionalLong.of(23500)),
    SUNSET("sunset", OptionalLong.of(12500));

    private final String name;
    private final OptionalLong time;

    MultiverseTime(String name, OptionalLong time) {
        this.name = name;
        this.time = time;
    }

    @Nullable
    public static MultiverseTime byName(String name) {
        for (MultiverseTime type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public OptionalLong getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

}
