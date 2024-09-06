package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.client.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.entities.RiftEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class RiftRenderer extends EntityRenderer<RiftEntity> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Multiverse.MOD_ID, "textures/block/rift_background.png");
    private static final RenderType TYPE = RenderType.create("rift", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(ClientHelper::getRiftShader)).setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(BACKGROUND, false, false).add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build()).createCompositeState(false));

    public RiftRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(RiftEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(-pEntity.getX(), -pEntity.getY(), -pEntity.getZ());
        int color = MultiverseColorHelper.getColor(pEntity.level, pEntity.getTarget());
        Vec3[][] vertices = pEntity.getShape().getVertices();
        VertexConsumer consumer = pBuffer.getBuffer(TYPE);
        Matrix4f matrix = pPoseStack.last().pose();
        for(int i = 0; i < 2; i ++) {
            for(int j = 0; j < 2; j ++) {
                consumer.vertex(matrix, (float) vertices[j][i].x(), (float) vertices[j][i].y(), (float) vertices[j][i].z()).color(color).endVertex();
            }
        }
        for(int i = 1; i >= 0; i --) {
            for(int j = 1; j >= 0; j --) {
                consumer.vertex(matrix, (float) vertices[j][i].x(), (float) vertices[j][i].y(), (float) vertices[j][i].z()).color(color).endVertex();
            }
        }
        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(RiftEntity entity) {
        return BACKGROUND;
    }

}
