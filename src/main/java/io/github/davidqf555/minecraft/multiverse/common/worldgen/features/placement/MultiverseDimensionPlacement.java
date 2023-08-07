package io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class MultiverseDimensionPlacement extends PlacementModifier {

    private static final MultiverseDimensionPlacement INSTANCE = new MultiverseDimensionPlacement();
    public static final Codec<MultiverseDimensionPlacement> CODEC = Codec.unit(INSTANCE);

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        return DimensionHelper.getIndex(context.getLevel().getLevel().dimension()) == 0 ? Stream.empty() : Stream.of(pos);
    }

    @Override
    public PlacementModifierType<?> type() {
        return FeatureRegistry.MULTIVERSE_DIMENSION;
    }

}
