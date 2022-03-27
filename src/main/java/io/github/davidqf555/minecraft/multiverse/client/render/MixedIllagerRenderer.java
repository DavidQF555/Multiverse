package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.SpellcasterIllager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MixedIllagerRenderer<T extends SpellcasterIllager> extends IllagerRenderer<T> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Multiverse.MOD_ID, "textures/entity/collector.png");

    public MixedIllagerRenderer(EntityRendererProvider.Context manager) {
        super(manager, new IllagerModel<>(manager.bakeLayer(ModelLayers.ILLUSIONER)), 0.5f);
        addLayer(new ItemInHandLayer<>(this) {
            @Override
            public void render(PoseStack matrix, MultiBufferSource buffer, int p_225628_3_, T entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
                if (entity.isCastingSpell() || entity.isAggressive()) {
                    super.render(matrix, buffer, p_225628_3_, entity, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
                }
            }
        });
        model.getHat().visible = true;
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
