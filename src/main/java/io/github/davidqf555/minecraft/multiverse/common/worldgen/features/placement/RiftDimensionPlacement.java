package io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.PlacementRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Random;
import java.util.stream.Stream;

public class RiftDimensionPlacement extends PlacementModifier {

    public static final RiftDimensionPlacement INSTANCE = new RiftDimensionPlacement();
    public static final Codec<RiftDimensionPlacement> CODEC = Codec.unit(INSTANCE);

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, Random random, BlockPos pos) {
        ResourceKey<Level> key = context.getLevel().getLevel().dimension();
        return key.equals(Level.OVERWORLD) || key.location().getNamespace().equals(Multiverse.MOD_ID) ? Stream.of(pos) : Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementRegistry.RIFT_DIMENSION.get();
    }
}
