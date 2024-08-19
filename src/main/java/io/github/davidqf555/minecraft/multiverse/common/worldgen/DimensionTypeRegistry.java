package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.collect.ImmutableMap;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

public final class DimensionTypeRegistry {

    public static final Map<ResourceLocation, DimensionType> TYPES;

    static {
        List<ShapeEntry> shapes = List.of(
                new ShapeEntry("islands", false, 0, 256, 0.1f),
                new ShapeEntry("normal", false, -64, 384, 0),
                new ShapeEntry("roofed", true, 0, 128, 0.2f)
        );
        List<EffectEntry> effects = new ArrayList<>();
        effects.add(new EffectEntry("overworld", DimensionType.OVERWORLD_EFFECTS, false));
        effects.add(new EffectEntry("end", DimensionType.END_EFFECTS, true));
        DimensionEffectsRegistry.FOG.keySet().forEach(effect -> effects.add(new EffectEntry(effect.getPath(), effect, false)));

        ImmutableMap.Builder<ResourceLocation, DimensionType> builder = ImmutableMap.builder();
        for (ShapeEntry shape : shapes) {
            boolean roofed = shape.roofed();
            for (MultiverseType type : MultiverseType.values()) {
                for (Time time : roofed ? new Time[]{Time.NIGHT} : Time.values()) {
                    String base = shape.name() + "/" + type.getName();
                    if (!roofed) {
                        base += "/" + time.name;
                    }
                    for (EffectEntry effect : effects) {
                        if (!effect.nightOnly() || time == Time.NIGHT) {
                            ResourceLocation loc = new ResourceLocation(Multiverse.MOD_ID, base + "/" + effect.name());
                            DimensionType val = DimensionType.create(time.time, !roofed, roofed, type.isUltrawarm(), type.isNatural(), 1, false, type.isPiglinSafe(), true, true, type.hasRaids(), shape.minY(), shape.height(), shape.height(), type.getInfiniburn(), effect.effect(), shape.lighting());
                            builder.put(loc, val);
                        }
                    }
                }
            }
        }
        TYPES = builder.build();
    }

    private DimensionTypeRegistry() {
    }

    private enum Time {

        DYNAMIC("dynamic", OptionalLong.empty()),
        DAY("day", OptionalLong.of(6000)),
        NIGHT("night", OptionalLong.of(18000)),
        SUNRISE("sunrise", OptionalLong.of(23500)),
        SUNSET("sunset", OptionalLong.of(12500));

        private final String name;
        private final OptionalLong time;

        Time(String name, OptionalLong time) {
            this.name = name;
            this.time = time;
        }

    }

    private record ShapeEntry(String name, boolean roofed, int minY, int height, float lighting) {
    }

    private record EffectEntry(String name, ResourceLocation effect, boolean nightOnly) {
    }

}
