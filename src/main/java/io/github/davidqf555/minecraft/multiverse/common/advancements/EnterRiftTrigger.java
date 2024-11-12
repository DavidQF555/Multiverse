package io.github.davidqf555.minecraft.multiverse.common.advancements;

import com.google.gson.JsonObject;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class EnterRiftTrigger extends SimpleCriterionTrigger<EnterRiftTrigger.Instance> {

    public static final ResourceLocation ID = new ResourceLocation(Multiverse.MOD_ID, "enter_rift");
    public static final EnterRiftTrigger INSTANCE = new EnterRiftTrigger();

    @Override
    protected Instance createInstance(JsonObject pJson, EntityPredicate.Composite pPlayer, DeserializationContext pContext) {
        return new Instance(pPlayer);
    }

    public void trigger(ServerPlayer pPlayer) {
        super.trigger(pPlayer, val -> true);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static class Instance extends AbstractCriterionTriggerInstance {

        public Instance(EntityPredicate.Composite pPlayer) {
            super(ID, pPlayer);
        }

    }
}
