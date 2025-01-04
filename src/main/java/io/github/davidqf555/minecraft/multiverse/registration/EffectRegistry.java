package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.effects.ConquerorEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class EffectRegistry {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Multiverse.MOD_ID);

    public static final RegistryObject<ConquerorEffect> BOUNTY = register("bounty", () -> new ConquerorEffect(MobEffectCategory.NEUTRAL, 0x00D5FF));

    private EffectRegistry() {
    }

    private static <T extends MobEffect> RegistryObject<T> register(String name, Supplier<T> effect) {
        return EFFECTS.register(name, effect);
    }

}
