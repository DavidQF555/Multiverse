package io.github.davidqf555.minecraft.multiverse.common.worldgen.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

import javax.annotation.Nullable;

public enum MultiverseEffect {

    OVERWORLD("overworld", BuiltinDimensionTypes.OVERWORLD_EFFECTS),
    THE_END("the_end", BuiltinDimensionTypes.END_EFFECTS),
    WHITE_FOG("fog/white", DimensionEffectsRegistry.FOG.get(DyeColor.WHITE)),
    ORANGE("fog/orange", DimensionEffectsRegistry.FOG.get(DyeColor.ORANGE)),
    MAGENTA("fog/magenta", DimensionEffectsRegistry.FOG.get(DyeColor.MAGENTA)),
    LIGHT_BLUE("fog/light_blue", DimensionEffectsRegistry.FOG.get(DyeColor.LIGHT_BLUE)),
    YELLOW("fog/yellow", DimensionEffectsRegistry.FOG.get(DyeColor.YELLOW)),
    LIME("fog/lime", DimensionEffectsRegistry.FOG.get(DyeColor.LIME)),
    PINK("fog/pink", DimensionEffectsRegistry.FOG.get(DyeColor.PINK)),
    GRAY("fog/gray", DimensionEffectsRegistry.FOG.get(DyeColor.GRAY)),
    LIGHT_GRAY("fog/light_gray", DimensionEffectsRegistry.FOG.get(DyeColor.LIGHT_GRAY)),
    CYAN("fog/cyan", DimensionEffectsRegistry.FOG.get(DyeColor.CYAN)),
    PURPLE("fog/purple", DimensionEffectsRegistry.FOG.get(DyeColor.PURPLE)),
    BLUE("fog/blue", DimensionEffectsRegistry.FOG.get(DyeColor.BLUE)),
    BROWN("fog/brown", DimensionEffectsRegistry.FOG.get(DyeColor.BROWN)),
    GREEN("fog/green", DimensionEffectsRegistry.FOG.get(DyeColor.GREEN)),
    RED("fog/red", DimensionEffectsRegistry.FOG.get(DyeColor.RED)),
    BLACK("fog/black", DimensionEffectsRegistry.FOG.get(DyeColor.BLACK));

    private final String name;
    private final ResourceLocation location;

    MultiverseEffect(String name, ResourceLocation location) {
        this.name = name;
        this.location = location;
    }

    @Nullable
    public static MultiverseEffect byName(String name) {
        for (MultiverseEffect type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getLocation() {
        return location;
    }

}

    
