package multiverse.common.events;

import multiverse.common.Multiverse;
import multiverse.common.advancements.EnterRiftTrigger;
import multiverse.common.world.capabilities.SummonedData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBus {

    private ModBus() {
    }

    @SubscribeEvent
    public static void onRegistryCapability(RegisterCapabilitiesEvent event) {
        event.register(SummonedData.class);
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> CriteriaTriggers.register(new ResourceLocation(Multiverse.MOD_ID, "enter_rift").toString(), EnterRiftTrigger.INSTANCE));
    }

}
