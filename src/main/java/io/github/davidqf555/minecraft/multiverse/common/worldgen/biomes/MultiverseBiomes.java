package io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Set;

public interface MultiverseBiomes {

    Set<ResourceKey<Biome>> getOverworldBiomes();

    Set<ResourceKey<Biome>> getNetherBiomes();

    Set<ResourceKey<Biome>> getEndBiomes();

    Climate.ParameterPoint getParameters(ResourceKey<Biome> biome);

    default boolean overrideVanillaSurface() {
        return true;
    }

    default Set<ResourceKey<Biome>> getBiomes(MultiverseType type) {
        return switch (type) {
            case NETHER -> getNetherBiomes();
            case END -> getEndBiomes();
            default -> getOverworldBiomes();
        };
    }

    SurfaceRules.RuleSource createSurface(MultiverseShape shape, MultiverseType type);

}
