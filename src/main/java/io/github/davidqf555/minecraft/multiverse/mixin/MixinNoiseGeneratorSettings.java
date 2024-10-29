package io.github.davidqf555.minecraft.multiverse.mixin;

import io.github.davidqf555.minecraft.multiverse.common.ConfigHelper;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.IMultiverseNoiseGeneratorSettings;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NoiseGeneratorSettings.class)
public class MixinNoiseGeneratorSettings implements IMultiverseNoiseGeneratorSettings {

    @Unique
    private SurfaceRules.RuleSource surface;
    @Unique
    private boolean floor, ceiling;
    @Unique
    private MultiverseType type;

    @Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true)
    private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> callback) {
        if (type != null) {
            if (surface == null) {
                surface = ConfigHelper.biomes.createSurface(floor, ceiling, type);
            }
            callback.setReturnValue(surface);
        }
    }

    @Override
    public void setSettings(boolean floor, boolean ceiling, MultiverseType type) {
        this.floor = floor;
        this.ceiling = ceiling;
        this.type = type;
        surface = null;
    }

}
