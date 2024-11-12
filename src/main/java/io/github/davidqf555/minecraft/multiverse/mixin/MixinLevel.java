package io.github.davidqf555.minecraft.multiverse.mixin;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Level.class)
public abstract class MixinLevel implements BiomeManager.NoiseBiomeSource {

    @Shadow
    private BiomeManager biomeManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(
            WritableLevelData p_270739_,
            ResourceKey<Level> dimension,
            RegistryAccess p_270200_,
            Holder<DimensionType> p_270240_,
            boolean p_270904_,
            boolean p_270470_,
            long biomeZoomSeed,
            int p_270466_,
            CallbackInfo callback
    ) {
        int index = DimensionHelper.getIndex(dimension);
        if (index > 0) {
            long seed = DimensionHelper.getSeed(biomeZoomSeed, index, true);
            biomeManager = new BiomeManager(this, BiomeManager.obfuscateSeed(seed));
        }
    }

}
