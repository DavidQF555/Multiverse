package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.Optional;
import java.util.stream.Stream;

public class RiftConfig implements IFeatureConfig {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.optionalFieldOf("target").forGetter(config -> config.target)
    ).apply(builder, builder.stable(RiftConfig::new)));
    public static final RiftConfig UNKNOWN = new RiftConfig(Optional.empty());

    private final Optional<Integer> target;

    private RiftConfig(Optional<Integer> target) {
        this.target = target;
    }

    public static RiftConfig of(int target) {
        return new RiftConfig(Optional.of(target));
    }

    public Optional<Integer> getTarget() {
        return target;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> getFeatures() {
        return IFeatureConfig.super.getFeatures();
    }
}
