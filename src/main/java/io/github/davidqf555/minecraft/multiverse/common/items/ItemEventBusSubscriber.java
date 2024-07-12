package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.registration.ItemRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ItemEventBusSubscriber {

    private ItemEventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onModifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        // needed because SmithingTemplateItem does not expose item properties
        event.modify(ItemRegistry.DIMENSIONAL_PRISM::get, builder -> builder.set(DataComponents.RARITY, Rarity.RARE));
    }

}
