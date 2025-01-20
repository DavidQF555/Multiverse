package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.blocks.RiftBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class BlockRegistry {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Multiverse.MOD_ID);

    public static final DeferredBlock<RiftBlock> RIFT = register("rift", RiftBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(-1, 3600000).sound(SoundType.GLASS).noLootTable().noTerrainParticles().lightLevel(state -> 15));
    public static final DeferredBlock<AmethystClusterBlock> KALEIDITE_CLUSTER = register("kaleidite_cluster", prop -> new AmethystClusterBlock(7, 3, prop), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.AMETHYST_CLUSTER).noOcclusion().strength(1.5f).lightLevel(state -> 5).pushReaction(PushReaction.DESTROY));

    private BlockRegistry() {
    }

    private static <T extends Block> DeferredBlock<T> register(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties base) {
        return BLOCKS.registerBlock(name, block, base);
    }
}
