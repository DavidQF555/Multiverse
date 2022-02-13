package io.github.davidqf555.minecraft.multiverse.common.world.rifts;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class RiftPlacement extends PlacementModifier {

    public static final RiftPlacement INSTANCE = new RiftPlacement();
    public static final ResourceLocation LOCATION = new ResourceLocation(Multiverse.MOD_ID, "rift");
    public static final Codec<RiftPlacement> CODEC = Codec.unit(INSTANCE);
    public static PlacementModifierType<RiftPlacement> TYPE = null;

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, Random random, BlockPos pos) {
        WorldGenLevel level = context.getLevel();
        ResourceKey<Level> key = level.getLevel().dimension();
        if (key.equals(Level.OVERWORLD) || key.location().getNamespace().equals(Multiverse.MOD_ID)) {
            int x = random.nextInt(16) + pos.getX();
            int z = random.nextInt(16) + pos.getZ();
            int y = random.nextInt(context.getGenDepth());
            BlockPos rift = new BlockPos(x, y, z);
            for (BlockPos.MutableBlockPos block = new BlockPos.MutableBlockPos().setWithOffset(rift, 0, -1, 0); block.getY() > 0; block.move(0, -1, 0)) {
                if (!level.isEmptyBlock(block)) {
                    return Stream.of(rift);
                }
            }
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return TYPE;
    }
}
