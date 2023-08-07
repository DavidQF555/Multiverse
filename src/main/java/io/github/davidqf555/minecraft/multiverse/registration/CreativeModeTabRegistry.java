package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class CreativeModeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Multiverse.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MULTIVERSE = register("main", () -> CreativeModeTab.builder().icon(() -> ItemRegistry.KALEIDITE_SHARD.get().getDefaultInstance()).title(Component.translatable("itemGroup." + Multiverse.MOD_ID)).build());

    private CreativeModeTabRegistry() {
    }

    private static RegistryObject<CreativeModeTab> register(String name, Supplier<CreativeModeTab> tab) {
        return TABS.register(name, tab);
    }

}
