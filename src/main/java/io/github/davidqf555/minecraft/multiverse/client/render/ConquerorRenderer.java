package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.ConquerorEntity;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.EvokerRenderState;
import net.minecraft.resources.ResourceLocation;

public class ConquerorRenderer extends IllagerRenderer<ConquerorEntity, EvokerRenderState> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "textures/entity/conqueror.png");

    public ConquerorRenderer(EntityRendererProvider.Context manager) {
        super(manager, new IllagerModel<>(manager.bakeLayer(ModelLayers.VINDICATOR)), 0.5f);
        addLayer(new ItemInHandLayer<>(this, manager.getItemRenderer()) {
            @Override
            public void render(PoseStack p_117193_, MultiBufferSource p_117194_, int p_117195_, EvokerRenderState p_365089_, float p_117197_, float p_117198_) {
                if (!p_365089_.isCastingSpell) {
                    super.render(p_117193_, p_117194_, p_117195_, p_365089_, p_117197_, p_117198_);
                }
            }
        });
        getModel().getHat().visible = true;
    }

    @Override
    public EvokerRenderState createRenderState() {
        return new EvokerRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(EvokerRenderState state) {
        return TEXTURE;
    }

}
