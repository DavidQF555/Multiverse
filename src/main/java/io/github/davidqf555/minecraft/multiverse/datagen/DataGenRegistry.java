package io.github.davidqf555.minecraft.multiverse.datagen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenRegistry {

    private DataGenRegistry() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        if (event.includeServer()) {
            DataGenerator gen = event.getGenerator();
            gen.addProvider(new NoiseGeneratorSettingsProvider(gen));
        }
    }

}
