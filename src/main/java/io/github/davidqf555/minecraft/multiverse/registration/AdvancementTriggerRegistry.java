package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.advancements.EnterRiftTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class AdvancementTriggerRegistry {

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, Multiverse.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, EnterRiftTrigger> ENTER_RIFT = register("enter_rift", EnterRiftTrigger::new);

    private AdvancementTriggerRegistry() {
    }

    private static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, T> register(String name, Supplier<T> trigger) {
        return TRIGGERS.register(name, trigger);
    }

}
