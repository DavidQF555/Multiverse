package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.SpellcastingIllagerEntity;
import net.minecraft.util.ResourceLocation;

public class MixedIllagerRenderer<T extends SpellcastingIllagerEntity> extends IllagerRenderer<T> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Multiverse.MOD_ID, "textures/entity/dimension_boss.png");

    public MixedIllagerRenderer(EntityRendererManager manager) {
        super(manager, new IllagerModel<>(0, 0, 64, 64), 0.5f);
        this.addLayer(new HeldItemLayer<T, IllagerModel<T>>(this) {
            @Override
            public void render(MatrixStack matrix, IRenderTypeBuffer buffer, int p_225628_3_, T entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
                if (entity.isCastingSpell() || entity.isAggressive()) {
                    super.render(matrix, buffer, p_225628_3_, entity, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
                }
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}