package io.github.davidqf555.minecraft.multiverse.common.world;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raider;

public interface PublicRaidType {

    EntityType<? extends Raider> getEntityType();

}
