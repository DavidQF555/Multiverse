package io.github.davidqf555.minecraft.multiverse.client.render;

import io.github.davidqf555.minecraft.multiverse.client.ClientConfigs;
import io.github.davidqf555.minecraft.multiverse.client.ShaderHelper;
import io.github.davidqf555.minecraft.multiverse.client.colors.MultiverseColorHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.function.BooleanSupplier;

public class MultiverseTransitionScreen extends ReceivingLevelScreen {

    private final int color;

    public MultiverseTransitionScreen(BooleanSupplier levelReceived, Reason reason, ResourceKey<Level> dim) {
        super(levelReceived, reason);
        color = MultiverseColorHelper.getColors(dim, 1)[0];
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(ClientConfigs.INSTANCE.vanillaOnly.get() ? ShaderHelper.RIFT_VANILLA : ShaderHelper.RIFT, 0, 0, width, height, color);
    }

}
