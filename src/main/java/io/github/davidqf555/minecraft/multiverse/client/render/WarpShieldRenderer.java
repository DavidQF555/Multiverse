package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public class WarpShieldRenderer implements NoDataSpecialModelRenderer {

    public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "warp_shield");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "textures/item/warp_shield.png");
    private final ShieldModel model;

    protected WarpShieldRenderer(ShieldModel model) {
        this.model = model;
    }

    @Override
    public void render(ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int light, int overlay, boolean foil) {
        pose.pushPose();
        pose.scale(1, -1, -1);
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer(buffer, model.renderType(TEXTURE), true, foil);
        model.renderToBuffer(pose, vertexconsumer, light, overlay);
        pose.popPose();
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        protected Unbaked() {
        }

        @Nullable
        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet set) {
            return new WarpShieldRenderer(new ShieldModel(set.bakeLayer(ModelLayers.SHIELD)));
        }

        @Override
        public MapCodec<? extends Unbaked> type() {
            return CODEC;
        }

    }

}
