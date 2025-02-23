package multiverse.common.util;

import com.google.common.collect.ImmutableList;
import multiverse.common.world.worldgen.biomes.MultiverseBiomes;
import multiverse.common.world.worldgen.biomes.VanillaMultiverseBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;

public final class MultiverseConfig {

    private static MultiverseBiomes biomes = VanillaMultiverseBiomes.INSTANCE;
    private static List<ResourceKey<Level>> dimensions = List.of();

    private MultiverseConfig() {
    }

    public static MultiverseBiomes getBiomesManager() {
        return biomes;
    }

    public static void setBiomesManager(MultiverseBiomes biomes) {
        MultiverseConfig.biomes = biomes;
    }

    public static List<ResourceKey<Level>> getTargetDimensions() {
        return dimensions;
    }

    public static void setTargetDimensions(List<ResourceKey<Level>> dimensions) {
        ImmutableList.Builder<ResourceKey<Level>> builder = ImmutableList.builder();
        dimensions.stream().sorted().forEach(builder::add);
        MultiverseConfig.dimensions = builder.build();
    }

}
