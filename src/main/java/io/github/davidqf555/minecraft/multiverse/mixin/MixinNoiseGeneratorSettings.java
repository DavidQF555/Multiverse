package io.github.davidqf555.minecraft.multiverse.mixin;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.IMultiverseNoiseGeneratorSettings;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomeTagsRegistry;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {NoiseGeneratorSettings.class})
public class MixinNoiseGeneratorSettings implements IMultiverseNoiseGeneratorSettings {

    private SurfaceRules.RuleSource surface;
    private MultiverseShape shape;
    private MultiverseType type;

    @Inject(method = {"surfaceRule"}, at = {@At("HEAD")}, cancellable = true)
    private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> callback) {
        if (shape != null && type != null) {
            if (surface == null) {
                surface = MultiverseBiomeTagsRegistry.getMultiverseBiomes().createSurface(shape, type);
            }
            callback.setReturnValue(surface);
        }
    }

    @Override
    public void setSettings(MultiverseShape shape, MultiverseType type) {
        this.shape = shape;
        this.type = type;
    }

}
