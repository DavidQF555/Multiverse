package io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.stream.Stream;

public class RiftDimensionPlacement extends Placement<NoPlacementConfig> {

    public RiftDimensionPlacement(Codec<NoPlacementConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random rand, NoPlacementConfig config, BlockPos pos) {
        RegistryKey<World> key = helper.level.getLevel().dimension();
        return key.equals(World.OVERWORLD) || key.location().getNamespace().equals(Multiverse.MOD_ID) ? Stream.of(pos) : Stream.empty();
    }
}
