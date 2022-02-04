package io.github.davidqf555.minecraft.multiverse.common.world.rifts;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.stream.Stream;

public class RiftPlacement extends Placement<NoPlacementConfig> {

    public static final ConfiguredPlacement<NoPlacementConfig> CONFIG = new RiftPlacement().configured(NoPlacementConfig.INSTANCE);

    public RiftPlacement() {
        super(NoPlacementConfig.CODEC);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random rand, NoPlacementConfig config, BlockPos pos) {
        RegistryKey<World> key = helper.level.getLevel().dimension();
        if (key.equals(World.OVERWORLD) || key.location().getNamespace().equals(Multiverse.MOD_ID)) {
            int x = rand.nextInt(16) + pos.getX();
            int z = rand.nextInt(16) + pos.getZ();
            int y = rand.nextInt(helper.getGenDepth());
            BlockPos rift = new BlockPos(x, y, z);
            for (BlockPos.Mutable block = new BlockPos.Mutable().setWithOffset(rift, 0, -1, 0); block.getY() > 0; block.move(0, -1, 0)) {
                if (!helper.level.isEmptyBlock(block)) {
                    return Stream.of(rift);
                }
            }
        }
        return Stream.empty();
    }
}
