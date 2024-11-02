package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Multiverse.MOD_ID);

    public static final DeferredHolder<Block, RiftBlock> RIFT = register("rift", RiftBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(-1, 3600000).noLootTable().noTerrainParticles().lightLevel(state -> 15));
    public static final DeferredHolder<Block, AmethystClusterBlock> KALEIDITE_CLUSTER = register("kaleidite_cluster", prop -> new AmethystClusterBlock(7, 3, prop), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.AMETHYST_CLUSTER).noOcclusion().strength(1.5f).lightLevel(state -> 5).pushReaction(PushReaction.DESTROY));

    private BlockRegistry() {
    }

    private static <T extends Block> DeferredHolder<Block, T> register(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties base) {
        return BLOCKS.register(name, () -> block.apply(base.setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, name)))));
    }
}
