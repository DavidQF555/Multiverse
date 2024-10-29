package io.github.davidqf555.minecraft.multiverse.common.worldgen.features;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class RiftFeature extends Feature<RiftConfig> {

    public RiftFeature(Codec<RiftConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RiftConfig> context) {
        WorldGenLevel reader = context.level();
        RiftConfig config = context.config();
        RandomSource rand = context.random();
        BlockState rift = config.getBlockState();
        Vec3 center = Vec3.atLowerCornerOf(context.origin()).add(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
        RiftConfig.Size size = config.getSize();
        double width = size.getWidth(rand);
        double height = size.getHeight(rand);
        RiftHelper.place(reader, rand, rift, Optional.empty(), Optional.empty(), center, width, height, false);
        return true;
    }

}
