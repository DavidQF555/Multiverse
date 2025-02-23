package multiverse.common.world.worldgen.features.placement;

import com.mojang.serialization.Codec;
import multiverse.registration.worldgen.PlacementRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class DimensionPlacement extends PlacementModifier {

    public static final Codec<DimensionPlacement> CODEC = ResourceKey.codec(Registries.DIMENSION).listOf().xmap(list -> new DimensionPlacement(Set.copyOf(list)), placement -> List.copyOf(placement.worlds)).fieldOf("dimensions").codec();
    private final Set<ResourceKey<Level>> worlds;

    public DimensionPlacement(Set<ResourceKey<Level>> worlds) {
        this.worlds = worlds;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext placementContext, RandomSource random, BlockPos blockPos) {
        ResourceKey<Level> dim = placementContext.getLevel().getLevel().dimension();
        return worlds.contains(dim) ? Stream.of(blockPos) : Stream.empty();
    }

    @Override
    public PlacementModifierType<? extends DimensionPlacement> type() {
        return PlacementRegistry.DIMENSION.get();
    }

}
