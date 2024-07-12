package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Multiverse.MOD_ID);

    public static final DeferredHolder<Block, RiftBlock> RIFT = register("rift", () -> new RiftBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).noCollission().noOcclusion().strength(-1, 3600000).noLootTable().noTerrainParticles().randomTicks().lightLevel(state -> 15)));
    public static final DeferredHolder<Block, AmethystClusterBlock> KALEIDITE_CLUSTER = register("kaleidite_cluster", () -> new AmethystClusterBlock(7, 3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.AMETHYST_CLUSTER).noOcclusion().strength(1.5f).lightLevel(state -> 5)));

    private BlockRegistry() {
    }

    private static <T extends Block> DeferredHolder<Block, T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
}
