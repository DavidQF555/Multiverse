package io.github.davidqf555.minecraft.multiverse.client.effects;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

public class ColoredFogEffect extends DimensionSpecialEffects.NetherEffects {

    private final Vec3 fog;

    public ColoredFogEffect(int color) {
        fog = Vec3.fromRGB24(color);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 biome, float time) {
        return biome.multiply(fog);
    }

}
