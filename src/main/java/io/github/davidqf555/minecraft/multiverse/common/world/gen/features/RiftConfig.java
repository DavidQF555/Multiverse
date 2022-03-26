package io.github.davidqf555.minecraft.multiverse.common.world.gen.features;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.Optional;
import java.util.Random;

public class RiftConfig implements IFeatureConfig {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.optionalFieldOf("target").forGetter(config -> config.target),
            BlockState.CODEC.fieldOf("block").forGetter(config -> config.block),
            Codec.BOOL.fieldOf("natural").forGetter(config -> config.natural),
            Codec.pair(Codec.INT, Codec.INT).fieldOf("width").forGetter(config -> config.width),
            Codec.pair(Codec.INT, Codec.INT).fieldOf("height").forGetter(config -> config.height),
            Codec.pair(Codec.FLOAT, Codec.FLOAT).fieldOf("xRot").forGetter(config -> config.xRot),
            Codec.pair(Codec.FLOAT, Codec.FLOAT).fieldOf("yRot").forGetter(config -> config.yRot),
            Codec.pair(Codec.FLOAT, Codec.FLOAT).fieldOf("zRot").forGetter(config -> config.zRot)
    ).apply(builder, builder.stable(RiftConfig::new)));
    private final Optional<Integer> target;
    private final BlockState block;
    private final boolean natural;
    private final Pair<Integer, Integer> width, height;
    private final Pair<Float, Float> xRot, yRot, zRot;

    public RiftConfig(Optional<Integer> target, BlockState block, boolean natural, Pair<Integer, Integer> width, Pair<Integer, Integer> height, Pair<Float, Float> xRot, Pair<Float, Float> yRot, Pair<Float, Float> zRot) {
        this.target = target;
        this.block = block;
        this.natural = natural;
        this.width = width;
        this.height = height;
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
    }

    public static RiftConfig of(Optional<Integer> target, BlockState block, boolean natural) {
        return new RiftConfig(target, block, natural, Pair.of(ServerConfigs.INSTANCE.minRiftWidth.get(), ServerConfigs.INSTANCE.maxRiftWidth.get()), Pair.of(ServerConfigs.INSTANCE.minRiftHeight.get(), ServerConfigs.INSTANCE.maxRiftHeight.get()), Pair.of(0f, 180f), Pair.of(0f, 180f), Pair.of(0f, 180f));
    }

    public static RiftConfig fixed(Optional<Integer> target, BlockState block, boolean natural, int width, int height, float xRot, float yRot, float zRot) {
        return new RiftConfig(target, block, natural, Pair.of(width, width), Pair.of(height, height), Pair.of(xRot, xRot), Pair.of(yRot, yRot), Pair.of(zRot, zRot));
    }

    public Optional<Integer> getTarget() {
        return target;
    }

    public BlockState getBlockState() {
        return block;
    }

    public boolean isNatural() {
        return natural;
    }

    public int getWidth(Random random) {
        Integer min = width.getFirst();
        if (min == null) {
            min = Integer.MIN_VALUE;
        }
        Integer max = width.getSecond();
        if (max == null) {
            max = Integer.MAX_VALUE;
        }
        return random.nextInt(max - min + 1) + min;
    }

    public int getHeight(Random random) {
        Integer min = height.getFirst();
        if (min == null) {
            min = Integer.MIN_VALUE;
        }
        Integer max = height.getSecond();
        if (max == null) {
            max = Integer.MAX_VALUE;
        }
        return random.nextInt(max - min + 1) + min;
    }

    public float getRotX(Random random) {
        Float min = xRot.getFirst();
        if (min == null) {
            min = Float.MIN_VALUE;
        }
        Float max = xRot.getSecond();
        if (max == null) {
            max = Float.MAX_VALUE;
        }
        return min + random.nextFloat() * (max - min);
    }

    public float getRotY(Random random) {
        Float min = yRot.getFirst();
        if (min == null) {
            min = Float.MIN_VALUE;
        }
        Float max = yRot.getSecond();
        if (max == null) {
            max = Float.MAX_VALUE;
        }
        return min + random.nextFloat() * (max - min);
    }

    public float getRotZ(Random random) {
        Float min = zRot.getFirst();
        if (min == null) {
            min = Float.MIN_VALUE;
        }
        Float max = zRot.getSecond();
        if (max == null) {
            max = Float.MAX_VALUE;
        }
        return min + random.nextFloat() * (max - min);
    }

}
