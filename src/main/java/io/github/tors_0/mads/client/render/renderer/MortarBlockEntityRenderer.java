package io.github.tors_0.mads.client.render.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.tors_0.mads.block.entity.MortarBlockEntity;
import io.github.tors_0.mads.client.render.model.MortarBlockEntityModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class MortarBlockEntityRenderer<T extends MortarBlockEntity, M extends MortarBlockEntityModel<T>> implements BlockEntityRenderer<T> {
    private final M model;

    public MortarBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super();
        model = (M) new MortarBlockEntityModel<>(context.getLayerModelPart(MortarBlockEntityModel.LAYER_LOCATION));
    }

    @Override
    public void render(MortarBlockEntity mortar, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        float h = mortar.getRotation();
        float m = mortar.getAngle();

        matrices.scale(-1.0F, -1.0F, 1.0F);
        matrices.translate(-0.5, -1.501F, 0.5);
        float n = 0.0F;
        float o = 0.0F;

        this.model.setAngles(mortar, o, n, mortar.getClientProgress(), h, m);
        RenderLayer renderLayer = RenderLayer.getEntitySolid(MortarBlockEntityModel.IDENTIFIER);
        if (renderLayer != null) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            int p = OverlayTexture.getUv(0,false);
            this.model.render(matrices, vertexConsumer, light, p, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        matrices.pop();
    }
}
