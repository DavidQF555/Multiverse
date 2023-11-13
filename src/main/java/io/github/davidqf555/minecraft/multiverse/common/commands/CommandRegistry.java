package io.github.davidqf555.minecraft.multiverse.common.commands;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommandRegistry {

    private CommandRegistry() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        MultiverseCommand.register(event.getDispatcher());
    }

}
