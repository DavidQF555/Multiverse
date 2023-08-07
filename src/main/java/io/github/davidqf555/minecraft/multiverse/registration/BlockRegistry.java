package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Multiverse.MOD_ID);

    public static final RegistryObject<RiftBlock> RIFT = register("rift", () -> new RiftBlock(BlockBehaviour.Properties.of(Material.PORTAL).noCollission().noOcclusion().strength(-1, 3600000).noLootTable().noParticlesOnBreak().randomTicks().lightLevel(state -> 15)));
    public static final RegistryObject<AmethystClusterBlock> KALEIDITE_CLUSTER = register("kaleidite_cluster", () -> new AmethystClusterBlock(7, 3, BlockBehaviour.Properties.of(Material.AMETHYST, MaterialColor.COLOR_LIGHT_BLUE).sound(SoundType.AMETHYST_CLUSTER).noOcclusion().strength(1.5f).lightLevel(state -> 5)));

    private BlockRegistry() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
}
