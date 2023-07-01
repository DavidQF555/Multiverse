package io.github.davidqf555.minecraft.multiverse.common.world.gen.features.placement;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.registration.worldgen.FeatureRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class AboveGroundPlacement extends PlacementModifier {

    public static final Codec<AboveGroundPlacement> CODEC = Codec.unit(() -> FeatureRegistry.ABOVE_GROUND);

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, Random rand, BlockPos pos) {
        int y = rand.nextInt(context.getGenDepth());
        BlockPos rift = new BlockPos(pos.getX(), y, pos.getZ());
        WorldGenLevel level = context.getLevel();
        for (BlockPos.MutableBlockPos block = new BlockPos.MutableBlockPos().setWithOffset(rift, 0, -1, 0); block.getY() > 0; block.move(0, -1, 0)) {
            if (!level.isEmptyBlock(block)) {
                return Stream.of(rift);
            }
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return FeatureRegistry.ABOVE_GROUND_PLACEMENT_TYPE;
    }
}