package io.github.davidqf555.minecraft.multiverse.datagen;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseTime;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.effects.MultiverseEffect;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DimensionTypesGenerator extends JsonCodecProvider<DimensionType> {

    public DimensionTypesGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper existingFileHelper) {
        super(output, PackOutput.Target.DATA_PACK, Registries.DIMENSION_TYPE.location().getPath(), PackType.SERVER_DATA, DimensionType.DIRECT_CODEC, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void gather() {
        for (MultiverseShape shape : MultiverseShape.values()) {
            Set<MultiverseTime> times = shape.getFixedTime().map(Set::of).orElseGet(() -> EnumSet.allOf(MultiverseTime.class));
            for (MultiverseType type : MultiverseType.values()) {
                for (MultiverseTime time : times) {
                    for (MultiverseEffect effect : MultiverseEffect.values()) {
                        conditions.put(shape.getTypeKey(type, time, effect).location(), new WithConditions<>(shape.createDimensionType(type, time, effect)));
                    }
                }
            }
        }
    }

}
