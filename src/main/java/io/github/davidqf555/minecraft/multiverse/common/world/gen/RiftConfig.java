package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.Optional;
import java.util.Random;

public class RiftConfig implements IFeatureConfig {

    private static final Codec<MinMaxBounds.IntBound> INT_BOUNDS_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.optionalFieldOf("min").forGetter(bounds -> Optional.ofNullable(bounds.getMin())),
            Codec.INT.optionalFieldOf("max").forGetter(bounds -> Optional.ofNullable(bounds.getMax()))
    ).apply(builder, builder.stable((min, max) -> new MinMaxBounds.IntBound(min.orElse(null), max.orElse(null)))));
    private static final Codec<MinMaxBounds.FloatBound> FLOAT_BOUNDS_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.FLOAT.optionalFieldOf("min").forGetter(bounds -> Optional.ofNullable(bounds.getMin())),
            Codec.FLOAT.optionalFieldOf("max").forGetter(bounds -> Optional.ofNullable(bounds.getMax()))
    ).apply(builder, builder.stable((min, max) -> new MinMaxBounds.FloatBound(min.orElse(null), max.orElse(null)))));
    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.optionalFieldOf("target").forGetter(config -> config.target),
            INT_BOUNDS_CODEC.fieldOf("width").forGetter(config -> config.width),
            INT_BOUNDS_CODEC.fieldOf("height").forGetter(config -> config.height),
            FLOAT_BOUNDS_CODEC.fieldOf("yaw").forGetter(config -> config.yaw),
            FLOAT_BOUNDS_CODEC.fieldOf("pitch").forGetter(config -> config.pitch),
            FLOAT_BOUNDS_CODEC.fieldOf("roll").forGetter(config -> config.roll),
            Codec.BOOL.fieldOf("temporary").forGetter(config -> config.temporary),
            Codec.BOOL.fieldOf("remove_near").forGetter(config -> config.removeNearby)
    ).apply(builder, builder.stable(RiftConfig::new)));
    private final Optional<Integer> target;
    private final MinMaxBounds.IntBound width, height;
    private final MinMaxBounds.FloatBound yaw, pitch, roll;
    private final boolean temporary, removeNearby;

    public RiftConfig(Optional<Integer> target, MinMaxBounds.IntBound width, MinMaxBounds.IntBound height, MinMaxBounds.FloatBound yaw, MinMaxBounds.FloatBound pitch, MinMaxBounds.FloatBound roll, boolean temporary, boolean removeNearby) {
        this.target = target;
        this.width = width;
        this.height = height;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        this.temporary = temporary;
        this.removeNearby = removeNearby;
    }

    public static RiftConfig of(Optional<Integer> target, boolean temporary) {
        return new RiftConfig(target, new MinMaxBounds.IntBound(1, 4), new MinMaxBounds.IntBound(6, 10), new MinMaxBounds.FloatBound(0f, 180f), new MinMaxBounds.FloatBound(0f, 180f), new MinMaxBounds.FloatBound(0f, 180f), temporary, true);
    }

    public Optional<Integer> getTarget() {
        return target;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public boolean shouldRemoveNearby() {
        return removeNearby;
    }

    public int getWidth(Random random) {
        int min = width.getMin() == null ? Integer.MIN_VALUE : width.getMin();
        int max = width.getMax() == null ? Integer.MAX_VALUE : width.getMax();
        return random.nextInt(max - min + 1) + min;
    }

    public int getHeight(Random random) {
        int min = height.getMin() == null ? Integer.MIN_VALUE : height.getMin();
        int max = height.getMax() == null ? Integer.MAX_VALUE : height.getMax();
        return random.nextInt(max - min + 1) + min;
    }

    public float getYaw(Random random) {
        float min = yaw.getMin() == null ? Float.MIN_VALUE : yaw.getMin();
        float max = yaw.getMax() == null ? Float.MAX_VALUE : yaw.getMax();
        return min + random.nextFloat() * (max - min);
    }

    public float getPitch(Random random) {
        float min = pitch.getMin() == null ? Float.MIN_VALUE : pitch.getMin();
        float max = pitch.getMax() == null ? Float.MAX_VALUE : pitch.getMax();
        return min + random.nextFloat() * (max - min);
    }

    public float getRoll(Random random) {
        float min = roll.getMin() == null ? Float.MIN_VALUE : roll.getMin();
        float max = roll.getMax() == null ? Float.MAX_VALUE : roll.getMax();
        return min + random.nextFloat() * (max - min);
    }

}
