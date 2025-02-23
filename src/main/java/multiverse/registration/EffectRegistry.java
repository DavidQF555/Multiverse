package multiverse.registration;

import multiverse.common.Multiverse;
import multiverse.common.world.effects.ConquerorEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class EffectRegistry {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Multiverse.MOD_ID);

    public static final DeferredHolder<MobEffect, ConquerorEffect> BOUNTY = register("bounty", () -> new ConquerorEffect(MobEffectCategory.NEUTRAL, 0x00D5FF));

    private EffectRegistry() {
    }

    private static <T extends MobEffect> DeferredHolder<MobEffect, T> register(String name, Supplier<T> effect) {
        return EFFECTS.register(name, effect);
    }

}
