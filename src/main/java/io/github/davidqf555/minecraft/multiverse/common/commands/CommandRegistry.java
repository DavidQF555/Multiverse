package io.github.davidqf555.minecraft.multiverse.common.commands;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class CommandRegistry {

    private CommandRegistry() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        MultiverseCommand.register(event.getDispatcher());
    }

}
