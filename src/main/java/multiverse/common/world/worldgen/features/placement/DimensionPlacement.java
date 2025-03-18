package multiverse.common.world.worldgen.features.placement;

import com.mojang.serialization.Codec;
import multiverse.common.world.DimensionList;
import multiverse.registration.worldgen.PlacementRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Random;
import java.util.stream.Stream;

public class DimensionPlacement extends PlacementModifier {

    public static final Codec<DimensionPlacement> CODEC = DimensionList.CODEC.xmap(DimensionPlacement::new, placement -> placement.list).fieldOf("dimensions").codec();
    private final Holder<DimensionList> list;

    public DimensionPlacement(Holder<DimensionList> list) {
        this.list = list;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext placementContext, Random random, BlockPos blockPos) {
        ResourceKey<Level> key = placementContext.getLevel().getLevel().dimension();
        if (list.value().contains(key)) {
            return Stream.of(blockPos);
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<? extends DimensionPlacement> type() {
        return PlacementRegistry.DIMENSION.get();
    }

}
