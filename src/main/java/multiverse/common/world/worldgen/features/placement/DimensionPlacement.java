package multiverse.common.world.worldgen.features.placement;

import com.mojang.serialization.MapCodec;
import multiverse.common.world.DimensionsList;
import multiverse.registration.worldgen.PlacementRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class DimensionPlacement extends PlacementModifier {

    public static final MapCodec<DimensionPlacement> CODEC = DimensionsList.CODEC.xmap(DimensionPlacement::new, placement -> placement.list).fieldOf("dimensions");
    private final DimensionsList list;

    public DimensionPlacement(DimensionsList list) {
        this.list = list;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext placementContext, RandomSource random, BlockPos blockPos) {
        ResourceLocation dim = placementContext.getLevel().getLevel().dimension().location();
        if ((list.operation() == DimensionsList.ListOperation.WHITELIST) == list.values().contains(dim)) {
            return Stream.of(blockPos);
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<? extends DimensionPlacement> type() {
        return PlacementRegistry.DIMENSION.get();
    }

}
