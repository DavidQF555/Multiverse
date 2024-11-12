package io.github.davidqf555.minecraft.multiverse.common.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class EnterRiftTrigger extends SimpleCriterionTrigger<EnterRiftTrigger.Instance> {

    public static final EnterRiftTrigger INSTANCE = new EnterRiftTrigger();

    @Override
    protected Instance createInstance(JsonObject pJson, Optional<ContextAwarePredicate> pPlayer, DeserializationContext pContext) {
        return new Instance(pPlayer);
    }

    public void trigger(ServerPlayer pPlayer) {
        super.trigger(pPlayer, val -> true);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {

        public Instance(Optional<ContextAwarePredicate> pPlayer) {
            super(pPlayer);
        }

    }
}
