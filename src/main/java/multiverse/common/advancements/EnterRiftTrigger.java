package multiverse.common.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class EnterRiftTrigger extends SimpleCriterionTrigger<EnterRiftTrigger.Instance> {

    public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player)
            )
            .apply(inst, Instance::new));

    public static final EnterRiftTrigger INSTANCE = new EnterRiftTrigger();

    public void trigger(ServerPlayer pPlayer) {
        super.trigger(pPlayer, val -> true);
    }

    @Override
    public Codec<Instance> codec() {
        return CODEC;
    }

    public record Instance(Optional<ContextAwarePredicate> player) implements SimpleInstance {
    }

}
