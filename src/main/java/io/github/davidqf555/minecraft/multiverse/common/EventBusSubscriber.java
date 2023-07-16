package io.github.davidqf555.minecraft.multiverse.common;

import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EventBusSubscriber {

    private EventBusSubscriber() {
    }
//
//    @SubscribeEvent
//    public static void onServerStarting(ServerStartingEvent event) {
//        MinecraftServer server = event.getServer();
//        Registry<LevelStem> registry = server.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
//        for (int index : MultiverseExistingData.getOrCreate(server).getExisting()) {
//            ResourceLocation loc = DimensionHelper.getRegistryKey(index).location();
//            if (!registry.containsKey(loc)) {
//                Registry.register(registry, loc, DimensionHelper.createDimension(server, index));
//            }
//        }
//    }

}
