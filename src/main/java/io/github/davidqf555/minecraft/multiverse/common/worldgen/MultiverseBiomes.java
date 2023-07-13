package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.HashSet;
import java.util.Set;

public interface MultiverseBiomes {

    Set<ResourceKey<Biome>> getOverworldBiomes();

    Set<ResourceKey<Biome>> getNetherBiomes();

    Set<ResourceKey<Biome>> getEndBiomes();

    default Set<ResourceKey<Biome>> getMixedBiomes() {
        Set<ResourceKey<Biome>> set = new HashSet<>();
        set.addAll(getOverworldBiomes());
        set.addAll(getNetherBiomes());
        set.addAll(getEndBiomes());
        return set;
    }

    default Set<ResourceKey<Biome>> getBiomes(MultiverseType type) {
        return switch (type) {
            case MIXED -> getMixedBiomes();
            case NETHER -> getNetherBiomes();
            case END -> getEndBiomes();
            default -> getOverworldBiomes();
        };
    }

    SurfaceRules.RuleSource createSurface(MultiverseShape shape, MultiverseType type);

}
