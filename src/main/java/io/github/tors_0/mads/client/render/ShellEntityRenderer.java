package io.github.tors_0.mads.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.tors_0.mads.block.entity.MortarBlockEntity;
import io.github.tors_0.mads.client.render.model.MortarBlockEntityModel;
import io.github.tors_0.mads.client.render.model.ShellEntityModel;
import io.github.tors_0.mads.entity.ShellEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.MathHelper;

public class ShellEntityRenderer<T extends ShellEntity, M extends ShellEntityModel<T>> extends ProjectileEntityRenderer<T> {
    private final M model;

    public ShellEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        model = (M) new ShellEntityModel<>(context.getPart(ShellEntityModel.LAYER_LOCATION));
    }

    @Override
    public void render(T shell, float f, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();

        matrixStack.multiply(Axis.Y_POSITIVE.rotationDegrees(MathHelper.lerp(tickDelta, shell.prevYaw, shell.getYaw()) - 90.0F));
        matrixStack.multiply(Axis.Z_POSITIVE.rotationDegrees(MathHelper.lerp(tickDelta, shell.prevPitch, shell.getPitch()) - 90.0F));

        float h = shell.getYaw();
        float m = shell.getPitch();

        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        matrixStack.translate(0.0, -1.501F, 0.0);
        float n = 0.0F;
        float o = 0.0F;

        this.model.setAngles(shell, o, n, shell.age, h, m);
        RenderLayer renderLayer = RenderLayer.getEntitySolid(ShellEntityModel.IDENTIFIER);
        if (renderLayer != null) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            int p = OverlayTexture.getUv(0,false);
            this.model.render(matrixStack, vertexConsumer, i, p, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(T entity) {
        return ShellEntityModel.IDENTIFIER;
    }
}
