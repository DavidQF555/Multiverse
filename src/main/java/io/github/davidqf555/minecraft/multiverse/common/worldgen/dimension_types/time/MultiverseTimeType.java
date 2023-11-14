package io.github.davidqf555.minecraft.multiverse.common.worldgen.dimension_types.time;

import javax.annotation.Nullable;
import java.util.OptionalLong;

public enum MultiverseTimeType {

    DYNAMIC("dynamic", OptionalLong.empty()),
    DAY("day", OptionalLong.of(6000)),
    NIGHT("night", OptionalLong.of(18000)),
    SUNRISE("sunrise", OptionalLong.of(23500)),
    SUNSET("sunset", OptionalLong.of(12500));

    private final String name;
    private final OptionalLong time;

    MultiverseTimeType(String name, OptionalLong time) {
        this.name = name;
        this.time = time;
    }

    @Nullable
    public static MultiverseTimeType byName(String name) {
        for (MultiverseTimeType type : values()) {
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
