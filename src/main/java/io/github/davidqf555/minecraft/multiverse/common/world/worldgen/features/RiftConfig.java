package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Random;

public class RiftConfig implements FeatureConfiguration {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockState.CODEC.fieldOf("block").forGetter(config -> config.block),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("min_width").forGetter(size -> size.minWidth),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("max_width").forGetter(size -> size.maxWidth),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("min_height").forGetter(size -> size.minHeight),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("max_height").forGetter(size -> size.maxHeight)
    ).apply(builder, RiftConfig::new));
    private final BlockState block;
    private final double minWidth, maxWidth, minHeight, maxHeight;

    public RiftConfig(BlockState block, double minWidth, double maxWidth, double minHeight, double maxHeight) {
        this.block = block;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public static RiftConfig of(BlockState block) {
        return new RiftConfig(block, ServerConfigs.INSTANCE.minFeatRiftWidth.get(), ServerConfigs.INSTANCE.maxFeatRiftWidth.get(), ServerConfigs.INSTANCE.minFeatRiftHeight.get(), ServerConfigs.INSTANCE.maxFeatRiftHeight.get());
    }

    public BlockState getBlockState() {
        return block;
    }

    public double getWidth(Random random) {
        return random.nextDouble() * (maxWidth - minWidth) + minWidth;
    }

    public double getHeight(Random random) {
        return random.nextDouble() * (maxHeight - minHeight) + minHeight;
    }

}
