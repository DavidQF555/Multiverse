package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Set;

public interface MultiverseBiomes {

    Set<ResourceKey<Biome>> getOverworldBiomes();

    Set<ResourceKey<Biome>> getNetherBiomes();

    Set<ResourceKey<Biome>> getEndBiomes();

    Set<ResourceKey<Biome>> getMixedBiomes();

    default boolean overrideVanillaSurface() {
        return true;
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
