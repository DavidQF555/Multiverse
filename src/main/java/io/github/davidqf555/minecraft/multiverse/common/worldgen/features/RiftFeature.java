package io.github.davidqf555.minecraft.multiverse.common.worldgen.features;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.util.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.util.RiftPlacementHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Random;

@ParametersAreNonnullByDefault
public class RiftFeature extends Feature<RiftConfig> {

    public RiftFeature(Codec<RiftConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RiftConfig> context) {
        WorldGenLevel reader = context.level();
        RiftConfig config = context.config();
        Random rand = context.random();
        BlockState state = config.getBlockState();
        Vec3 center = Vec3.atLowerCornerOf(context.origin()).add(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
        RiftConfig.Size size = config.getSize();
        double width = size.getWidth(rand);
        double height = size.getHeight(rand);
        Vec3 normal = new Vec3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
        float angle = rand.nextFloat(180);
        ResourceKey<Level> target = DimensionHelper.randomMultiverseDimension(rand, Optional.of(reader.getLevel().dimension()));
        RiftPlacementHelper.place(reader, reader, state, target, center, normal, angle, width, height, false, pos -> {
        });
        return true;
    }

}
