package io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class AboveGroundPlacement extends Placement<NoPlacementConfig> {

    public AboveGroundPlacement(Codec<NoPlacementConfig> codec) {
        super(codec);
    }

    @Nonnull
    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random rand, NoPlacementConfig config, BlockPos pos) {
        int y = rand.nextInt(helper.getGenDepth());
        BlockPos rift = new BlockPos(pos.getX(), y, pos.getZ());
        for (BlockPos.Mutable block = new BlockPos.Mutable().setWithOffset(rift, 0, -1, 0); block.getY() > 0; block.move(0, -1, 0)) {
            if (!helper.level.isEmptyBlock(block)) {
                return Stream.of(rift);
            }
        }
        return Stream.empty();
    }
}
