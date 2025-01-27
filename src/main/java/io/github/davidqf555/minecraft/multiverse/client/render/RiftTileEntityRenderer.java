package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import io.github.davidqf555.minecraft.multiverse.client.ClientConfigs;
import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.client.colors.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.blocks.RiftTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RiftTileEntityRenderer implements BlockEntityRenderer<RiftTileEntity> {

    private static final RenderType RIFT = RenderType.create(new ResourceLocation(Multiverse.MOD_ID, "rift").toString(), DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(ClientHelper::getRiftShader))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build())
                    .createCompositeState(false)
    );
    private static final RenderType VANILLA = RenderType.create(new ResourceLocation(Multiverse.MOD_ID, "rift_vanilla").toString(), DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build())
                    .createCompositeState(false)
    );

    @Override
    public void render(RiftTileEntity entity, float partial, PoseStack matrixStack, MultiBufferSource buffer, int overlay, int packedLight) {
        int[] base = entity.hasLevel() ? MultiverseColorHelper.getColors(entity.getLevel(), entity.getTarget(), 2) : new int[]{0xFFFFFF, 0xFFFFFF};
        VertexConsumer consumer = buffer.getBuffer(ClientConfigs.INSTANCE.vanillaOnly.get() ? VANILLA : RIFT);
        Vec3[][] visual = entity.getVisual();
        Vec3 offset = entity.getNormal().normalize().scale(ClientConfigs.INSTANCE.riftZOffset.get());
        double min = ClientConfigs.INSTANCE.riftMinOpacity.get();
        double max = ClientConfigs.INSTANCE.riftMaxOpacity.get();
        double[][] colors = calculateColors(visual.length, min, max, base[0], base[1]);
        matrixStack.pushPose();
        for (int i = visual.length - 1; i >= 0; i--) {
            drawPolygon(consumer, matrixStack, visual[i], offset, (float) colors[i][0], (float) colors[i][1], (float) colors[i][2], (float) colors[i][3], true);
        }
        matrixStack.popPose();
        matrixStack.pushPose();
        for (int i = visual.length - 1; i >= 0; i--) {
            drawPolygon(consumer, matrixStack, visual[i], offset, (float) colors[i][0], (float) colors[i][1], (float) colors[i][2], (float) colors[i][3], false);
        }
        matrixStack.popPose();
    }

    protected double[][] calculateColors(int layers, double minA, double maxA, int center, int edge) {
        double[][] colors = new double[layers][4];
        double destA = 0;
        for (int i = layers - 1; i >= 0; i--) {
            if (destA >= 1) {
                colors[i][0] = 1;
            } else {
                double next = Mth.lerp(getAlphaFactor(i, layers), minA, maxA);
                colors[i][0] = (next - destA) / (1 - destA);
                destA = next;
            }
        }
        double[] target = new double[]{FastColor.ARGB32.red(center) / 255.0, FastColor.ARGB32.green(center) / 255.0, FastColor.ARGB32.blue(center) / 255.0};
        double[] rate = new double[]{
                (target[0] - FastColor.ARGB32.red(edge) / 255.0) / (layers - 1.0),
                (target[1] - FastColor.ARGB32.green(edge) / 255.0) / (layers - 1.0),
                (target[2] - FastColor.ARGB32.blue(edge) / 255.0) / (layers - 1.0)
        };
        for (int i = 0; i < layers; i++) {
            if (colors[i][0] == 0) {
                continue;
            }
            for (int j = 0; j < 3; j++) {
                double scale = 1 / colors[i][0] - i - 1;
                double color = target[j] + rate[j] * scale;
                double factor;
                if (color < 0) {
                    factor = 1 - color / scale / rate[j];
                } else if (color > 1) {
                    factor = 1 - (color - 1) / scale / rate[j];
                } else {
                    continue;
                }
                rate[0] *= factor;
                rate[1] *= factor;
                rate[2] *= factor;
            }
        }
        for (int i = 0; i < layers; i++) {
            double scale = 1 / colors[i][0] - i - 1;
            for (int j = 0; j < 3; j++) {
                colors[i][j + 1] = target[j] + rate[j] * scale;
            }
        }
        return colors;
    }

    protected double getAlphaFactor(int layer, int layers) {
        return layers <= 1 ? 1 : 1 - layer / (layers - 1.0);
    }

    private void drawPolygon(VertexConsumer consumer, PoseStack pose, Vec3[] vertices, Vec3 offset, float alpha, float red, float green, float blue, boolean forward) {
        switch (vertices.length) {
            case 3:
                drawQuad(consumer, pose, vertices[0], vertices[0], vertices[1], vertices[2], offset, alpha, red, green, blue, forward);
                break;
            case 5:
                drawQuad(consumer, pose, vertices[0], vertices[0], vertices[3], vertices[4], offset, alpha, red, green, blue, forward);
            case 4:
                drawQuad(consumer, pose, vertices[0], vertices[1], vertices[2], vertices[3], offset, alpha, red, green, blue, forward);
                break;
            case 6:
                drawQuad(consumer, pose, vertices[0], vertices[1], vertices[2], vertices[3], offset, alpha, red, green, blue, forward);
                drawQuad(consumer, pose, vertices[0], vertices[3], vertices[4], vertices[5], offset, alpha, red, green, blue, forward);
                break;
            default:
                int jump = (vertices.length - 4) / 2;
                drawQuad(consumer, pose, vertices[0], vertices[1], vertices[jump + 2], vertices[jump + 3], offset, alpha, red, green, blue, forward);
                Vec3[] first = new Vec3[jump + 2];
                first[0] = vertices[jump + 2];
                first[1] = vertices[1];
                System.arraycopy(vertices, 2, first, 2, jump);
                drawPolygon(consumer, pose, first, offset, alpha, red, green, blue, forward);
                Vec3[] second = new Vec3[vertices.length - jump - 2];
                second[0] = vertices[0];
                second[1] = vertices[jump + 3];
                System.arraycopy(vertices, jump + 4, second, 2, vertices.length - jump - 4);
                drawPolygon(consumer, pose, second, offset, alpha, red, green, blue, forward);
            case 0:
            case 1:
            case 2:
        }
    }

    private void drawQuad(VertexConsumer consumer, PoseStack stack, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4, Vec3 offset, float alpha, float red, float green, float blue, boolean forward) {
        Matrix4f pose = stack.last().pose();
        if (forward) {
            consumer.vertex(pose, (float) p4.x(), (float) p4.y(), (float) p4.z()).color(red, green, blue, alpha).endVertex();
            consumer.vertex(pose, (float) p3.x(), (float) p3.y(), (float) p3.z()).color(red, green, blue, alpha).endVertex();
            consumer.vertex(pose, (float) p2.x(), (float) p2.y(), (float) p2.z()).color(red, green, blue, alpha).endVertex();
            consumer.vertex(pose, (float) p1.x(), (float) p1.y(), (float) p1.z()).color(red, green, blue, alpha).endVertex();
            stack.translate(offset.x(), offset.y(), offset.z());
        } else {
            consumer.vertex(pose, (float) p1.x(), (float) p1.y(), (float) p1.z()).color(red, green, blue, alpha).endVertex();
            consumer.vertex(pose, (float) p2.x(), (float) p2.y(), (float) p2.z()).color(red, green, blue, alpha).endVertex();
            consumer.vertex(pose, (float) p3.x(), (float) p3.y(), (float) p3.z()).color(red, green, blue, alpha).endVertex();
            consumer.vertex(pose, (float) p4.x(), (float) p4.y(), (float) p4.z()).color(red, green, blue, alpha).endVertex();
            stack.translate(-offset.x(), -offset.y(), -offset.z());
        }
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

}