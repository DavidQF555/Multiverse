package multiverse.mixin;

import multiverse.common.world.PublicRaidType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(value = Raid.RaiderType.class)
public class MixinRaidType implements PublicRaidType {

    @Shadow
    private Supplier<EntityType<? extends Raider>> entityTypeSupplier;

    @Override
    public EntityType<? extends Raider> getEntityType() {
        return entityTypeSupplier.get();
    }

}
