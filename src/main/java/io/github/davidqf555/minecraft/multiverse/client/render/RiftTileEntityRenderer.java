package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import io.github.davidqf555.minecraft.multiverse.client.ClientConfigs;
import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.client.colors.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.blocks.RiftTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RiftTileEntityRenderer implements BlockEntityRenderer<RiftTileEntity> {

    private static final RenderType RIFT = RenderType.create("rift", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(ClientHelper::getRiftShader))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(ClientHelper.RIFT, false, false).build())
                    .createCompositeState(false)
    );
    private static final RenderType VANILLA = RenderType.create("rift_vanilla", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(ClientHelper.RIFT, false, false).build())
                    .createCompositeState(false)
    );

    @Override
    public void render(RiftTileEntity entity, float partial, PoseStack matrixStack, MultiBufferSource buffer, int overlay, int packedLight) {
        int base = entity.hasLevel() ? MultiverseColorHelper.getColor(entity.getLevel(), entity.getTarget()) : 0xFFFFFF;
        VertexConsumer consumer = buffer.getBuffer(ClientConfigs.INSTANCE.vanillaOnly.get() ? VANILLA : RIFT);
        Vec3[][] visual = entity.getVisual();
        Vec3 offset = entity.getNormal().normalize().scale(ClientConfigs.INSTANCE.riftZOffset.get());
        double min = ClientConfigs.INSTANCE.riftMinOpacity.get();
        double max = ClientConfigs.INSTANCE.riftMaxOpacity.get();
        int[] colors = new int[visual.length];
        double destA = 0;
        for (int i = visual.length - 1; i >= 0; i--) {
            int alpha;
            if (destA >= 1) {
                alpha = 0xFF;
            } else {
                double target = getAlphaFactor(i, visual.length, min, max);
                alpha = (int) ((target - destA) / (1 - destA));
                destA += alpha * (1 - destA) / 0xFF;
            }
            colors[i] = (base & ~(0xFF << 24)) | (alpha << 24);
        }
        matrixStack.pushPose();
        for (int i = visual.length - 1; i >= 0; i--) {
            drawPolygon(consumer, matrixStack, visual[i], offset, colors[i], true);
        }
        matrixStack.popPose();
        matrixStack.pushPose();
        for (int i = visual.length - 1; i >= 0; i--) {
            drawPolygon(consumer, matrixStack, visual[i], offset, colors[i], false);
        }
        matrixStack.popPose();
    }

    protected double getAlphaFactor(int layer, int layers, double min, double max) {
        return layers <= 1 ? max : Mth.lerp(layer / (layers - 1.0), max, min);
    }

    private void drawPolygon(VertexConsumer consumer, PoseStack pose, Vec3[] vertices, Vec3 offset, int color, boolean forward) {
        switch (vertices.length) {
            case 3:
                drawQuad(consumer, pose, vertices[0], vertices[0], vertices[1], vertices[2], offset, color, forward);
                break;
            case 5:
                drawQuad(consumer, pose, vertices[0], vertices[0], vertices[3], vertices[4], offset, color, forward);
            case 4:
                drawQuad(consumer, pose, vertices[0], vertices[1], vertices[2], vertices[3], offset, color, forward);
                break;
            case 6:
                drawQuad(consumer, pose, vertices[0], vertices[1], vertices[2], vertices[3], offset, color, forward);
                drawQuad(consumer, pose, vertices[0], vertices[3], vertices[4], vertices[5], offset, color, forward);
                break;
            default:
                int jump = (vertices.length - 4) / 2;
                drawQuad(consumer, pose, vertices[0], vertices[1], vertices[jump + 2], vertices[jump + 3], offset, color, forward);
                Vec3[] first = new Vec3[jump + 2];
                first[0] = vertices[jump + 2];
                first[1] = vertices[1];
                System.arraycopy(vertices, 2, first, 2, jump);
                drawPolygon(consumer, pose, first, offset, color, forward);
                Vec3[] second = new Vec3[vertices.length - jump - 2];
                second[0] = vertices[0];
                second[1] = vertices[jump + 3];
                System.arraycopy(vertices, jump + 4, second, 2, vertices.length - jump - 4);
                drawPolygon(consumer, pose, second, offset, color, forward);
            case 0:
            case 1:
            case 2:
        }
    }

    private void drawQuad(VertexConsumer consumer, PoseStack stack, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4, Vec3 offset, int color, boolean forward) {
        Matrix4f pose = stack.last().pose();
        if (forward) {
            consumer.vertex(pose, (float) p4.x(), (float) p4.y(), (float) p4.z()).color(color).endVertex();
            consumer.vertex(pose, (float) p3.x(), (float) p3.y(), (float) p3.z()).color(color).endVertex();
            consumer.vertex(pose, (float) p2.x(), (float) p2.y(), (float) p2.z()).color(color).endVertex();
            consumer.vertex(pose, (float) p1.x(), (float) p1.y(), (float) p1.z()).color(color).endVertex();
            stack.translate(offset.x(), offset.y(), offset.z());
        } else {
            consumer.vertex(pose, (float) p1.x(), (float) p1.y(), (float) p1.z()).color(color).endVertex();
            consumer.vertex(pose, (float) p2.x(), (float) p2.y(), (float) p2.z()).color(color).endVertex();
            consumer.vertex(pose, (float) p3.x(), (float) p3.y(), (float) p3.z()).color(color).endVertex();
            consumer.vertex(pose, (float) p4.x(), (float) p4.y(), (float) p4.z()).color(color).endVertex();
            stack.translate(-offset.x(), -offset.y(), -offset.z());
        }
    }

    @Override
    public int getViewDistance() {
        return ClientConfigs.INSTANCE.riftRenderDistance.get();
    }

}