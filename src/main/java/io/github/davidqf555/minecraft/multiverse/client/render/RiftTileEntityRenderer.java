package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.client.colors.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RiftTileEntityRenderer implements BlockEntityRenderer<RiftTileEntity> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Multiverse.MOD_ID, "textures/block/rift_background.png");
    private static final RenderType TYPE = RenderType.create("rift", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(ClientHelper::getRiftShader)).setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(BACKGROUND, false, false).add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build()).createCompositeState(false));

    @Override
    public void render(RiftTileEntity entity, float partial, PoseStack matrixStack, MultiBufferSource buffer, int overlay, int packedLight) {
        int color = entity.hasLevel() ? MultiverseColorHelper.getColor(entity.getLevel(), entity.getTarget()) : 0xFFFFFF;
        matrixStack.pushPose();
        BlockPos pos = entity.getBlockPos();
        matrixStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
        Matrix4f pose = matrixStack.last().pose();
        Vec3[] vertices = entity.getVertices()[0];
        VertexConsumer consumer = buffer.getBuffer(TYPE);
        drawPolygon(consumer, pose, vertices, color);
        matrixStack.popPose();
    }

    private void drawPolygon(VertexConsumer consumer, Matrix4f pose, Vec3[] vertices, int color) {
        if (vertices.length == 3) {
            drawQuad(consumer, pose, vertices[0], vertices[0], vertices[1], vertices[2], color);
        } else if (vertices.length == 4) {
            drawQuad(consumer, pose, vertices[0], vertices[1], vertices[2], vertices[3], color);
        } else if (vertices.length == 5) {
            drawQuad(consumer, pose, vertices[0], vertices[1], vertices[2], vertices[3], color);
            drawQuad(consumer, pose, vertices[0], vertices[0], vertices[3], vertices[4], color);
        } else if (vertices.length == 6) {
            drawQuad(consumer, pose, vertices[0], vertices[1], vertices[2], vertices[3], color);
            drawQuad(consumer, pose, vertices[0], vertices[3], vertices[4], vertices[5], color);
        } else if (vertices.length > 6) {
            int jump = (vertices.length - 4) / 2;
            drawQuad(consumer, pose, vertices[0], vertices[1], vertices[jump + 2], vertices[jump + 3], color);
            Vec3[] first = new Vec3[jump + 2];
            first[0] = vertices[jump + 2];
            first[1] = vertices[1];
            System.arraycopy(vertices, 2, first, 2, jump);
            drawPolygon(consumer, pose, first, color);
            Vec3[] second = new Vec3[vertices.length - jump - 2];
            second[0] = vertices[0];
            second[1] = vertices[jump + 3];
            System.arraycopy(vertices, jump + 4, second, 2, vertices.length - jump - 4);
            drawPolygon(consumer, pose, second, color);
        }
    }

    private void drawQuad(VertexConsumer consumer, Matrix4f pose, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4, int color) {
        consumer.vertex(pose, (float) p1.x(), (float) p1.y(), (float) p1.z()).color(color).endVertex();
        consumer.vertex(pose, (float) p2.x(), (float) p2.y(), (float) p2.z()).color(color).endVertex();
        consumer.vertex(pose, (float) p3.x(), (float) p3.y(), (float) p3.z()).color(color).endVertex();
        consumer.vertex(pose, (float) p4.x(), (float) p4.y(), (float) p4.z()).color(color).endVertex();

        consumer.vertex(pose, (float) p4.x(), (float) p4.y(), (float) p4.z()).color(color).endVertex();
        consumer.vertex(pose, (float) p3.x(), (float) p3.y(), (float) p3.z()).color(color).endVertex();
        consumer.vertex(pose, (float) p2.x(), (float) p2.y(), (float) p2.z()).color(color).endVertex();
        consumer.vertex(pose, (float) p1.x(), (float) p1.y(), (float) p1.z()).color(color).endVertex();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

}