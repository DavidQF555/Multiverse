package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Multiverse.MOD_ID);

    public static final RegistryObject<RiftBlock> RIFT = register("rift", () -> new RiftBlock(BlockBehaviour.Properties.of(Material.PORTAL).noCollission().strength(-1, 3600000).noDrops().randomTicks().lightLevel(state -> 15)));
    public static final RegistryObject<OreBlock> KALEIDIUM_ORE = register("kaleidium_ore", () -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3, 3)));
    public static final RegistryObject<OreBlock> NETHERRACK_KALEIDIUM_ORE = register("netherrack_kaleidium_ore", () -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.NETHER).requiresCorrectToolForDrops().strength(3, 3)));
    public static final RegistryObject<OreBlock> END_STONE_KALEIDIUM_ORE = register("end_stone_kaleidium_ore", () -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND).requiresCorrectToolForDrops().strength(3, 3)));

    private BlockRegistry() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
}
