package io.github.davidqf555.minecraft.multiverse.mixin;

import io.github.davidqf555.minecraft.multiverse.client.render.MultiverseTransitionScreen;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.DimensionTransitionScreenManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DimensionTransitionScreenManager.class)
public class MixinDimensionTransitionScreenManager {

    @Inject(method = "getScreenFromLevel", at = @At("HEAD"), cancellable = true)
    private static void getScreenFromLevel(@Nullable Level to, @Nullable Level from, CallbackInfoReturnable<DimensionTransitionScreenManager.ReceivingLevelScreenFactory> callback) {
        if (to != null) {
            int index = DimensionHelper.getIndex(to.dimension()).orElse(-1);
            if (index > 0 || from != null && index == 0 && DimensionHelper.getIndex(from.dimension()).filter(i -> i > 0).isPresent()) {
                callback.setReturnValue(((supplier, reason) -> new MultiverseTransitionScreen(supplier, reason, to)));
            }
        }
    }

}
