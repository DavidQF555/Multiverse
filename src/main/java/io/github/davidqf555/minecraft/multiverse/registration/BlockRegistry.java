package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Multiverse.MOD_ID);

    public static final RegistryObject<RiftBlock> RIFT = register("rift", () -> new RiftBlock(AbstractBlock.Properties.of(Material.PORTAL).noCollission().strength(-1, 3600000).noDrops().randomTicks().lightLevel(state -> 15)));

    private BlockRegistry() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
}
