package io.github.davidqf555.minecraft.multiverse.client.render;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.entities.TravelerEntity;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class TravelerRenderer extends IllagerRenderer<TravelerEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Multiverse.MOD_ID, "textures/entity/traveler.png");

    public TravelerRenderer(EntityRendererProvider.Context manager) {
        super(manager, new IllagerModel<>(manager.bakeLayer(ModelLayers.PILLAGER)), 0.5f);
        addLayer(new ItemInHandLayer<>(this, manager.getItemInHandRenderer()));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull TravelerEntity traveler) {
        return TEXTURE;
    }

}
