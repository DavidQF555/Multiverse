package io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.PlacementRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class SolidPlacement extends PlacementModifier {

    public static final Codec<SolidPlacement> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Direction.CODEC.fieldOf("direction").forGetter(placement -> placement.dir),
            Codec.INT.fieldOf("steps").forGetter(placement -> placement.steps)
    ).apply(inst, SolidPlacement::of));
    private final Direction dir;
    private final int steps;
    private final BlockPredicate solid;

    protected SolidPlacement(Direction dir, int steps) {
        this.dir = dir;
        this.steps = steps;
        solid = BlockPredicate.solid(dir.getNormal());
    }

    public static SolidPlacement of(Direction dir, int steps) {
        return new SolidPlacement(dir, steps);
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        WorldGenLevel level = context.getLevel();
        BlockPos.MutableBlockPos mutable = pos.mutable();
        int step = 0;
        do {
            if (level.isOutsideBuildHeight(mutable) || !BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE.test(level, mutable)) {
                break;
            }
            if (solid.test(level, mutable)) {
                return Stream.of(mutable);
            }
            mutable.move(dir);
            step++;
        } while (step < steps);
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementRegistry.SOLID.get();
    }

}
