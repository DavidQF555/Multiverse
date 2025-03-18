package multiverse.common.world.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.common.ServerConfigs;
import multiverse.common.world.DimensionList;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class RiftConfig implements FeatureConfiguration {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DimensionList.CODEC.fieldOf("targets").forGetter(config -> config.targets),
            BlockState.CODEC.fieldOf("block").forGetter(config -> config.block),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("min_width").forGetter(size -> size.minWidth),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("max_width").forGetter(size -> size.maxWidth),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("min_height").forGetter(size -> size.minHeight),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("max_height").forGetter(size -> size.maxHeight)
    ).apply(builder, RiftConfig::new));
    private final Holder<DimensionList> targets;
    private final BlockState block;
    private final double minWidth, maxWidth, minHeight, maxHeight;

    public RiftConfig(Holder<DimensionList> targets, BlockState block, double minWidth, double maxWidth, double minHeight, double maxHeight) {
        this.targets = targets;
        this.block = block;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public static RiftConfig of(Holder<DimensionList> targets, BlockState block) {
        return new RiftConfig(targets, block, ServerConfigs.INSTANCE.minFeatRiftWidth.get(), ServerConfigs.INSTANCE.maxFeatRiftWidth.get(), ServerConfigs.INSTANCE.minFeatRiftHeight.get(), ServerConfigs.INSTANCE.maxFeatRiftHeight.get());
    }

    public Optional<ResourceKey<Level>> getTarget(Set<ResourceKey<Level>> registry, Random random, @Nullable ResourceKey<Level> exclude) {
        return targets.value().selectRandom(registry, random, exclude);
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
