package io.github.tors_0.mads.client.render.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.block.entity.MortarBlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class MortarBlockEntityModel<T extends MortarBlockEntity> extends Model {
    public static final Identifier IDENTIFIER = Mads.getId("textures/block/mortar.png");
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(IDENTIFIER, "main");
    private final ModelPart assembly;
    private final ModelPart mounts;
    private final ModelPart mortar;
    private final ModelPart barrel2;
    private final ModelPart bone;

    public MortarBlockEntityModel(ModelPart root) {
        super(null);
        this.assembly = root.getChild("assembly");
        this.mounts = assembly.getChild("mounts");
        this.mortar = assembly.getChild("mortar");
        this.barrel2 = mortar.getChild("barrel2");
        this.bone = root.getChild("bone");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData assembly = modelPartData.addChild("assembly", ModelPartBuilder.create()
                .uv(0, 18).cuboid(-7.0F, -0.5F, -7.0F, 14.0F, 1.0F, 14.0F),
                ModelTransform.of(0.0F, 21.5F, 0.0F,0,0,0));

        assembly.addChild("mounts", ModelPartBuilder.create()
                        .uv(42, 18).cuboid(-7.0F, -5.2222F, -5.0F, 3.0F, 2.0F, 10.0F)
                        .uv(42, 18).cuboid(4.0F, -5.2222F, -5.0F, 3.0F, 2.0F, 10.0F)
                        .uv(32, 33).cuboid(5.0F, -3.2222F, -5.0F, 1.0F, 10.0F, 10.0F)
                        .uv(32, 33).cuboid(-6.0F, -3.2222F, -5.0F, 1.0F, 10.0F, 10.0F)
                        .uv(0, 0).cuboid(-7.0F, -5.2222F, 5.0F, 3.0F, 12.0F, 2.0F)
                        .uv(0, 0).cuboid(-7.0F, -5.2222F, -7.0F, 3.0F, 12.0F, 2.0F)
                        .uv(0, 0).cuboid(4.0F, -5.2222F, -7.0F, 3.0F, 12.0F, 2.0F)
                        .uv(0, 0).cuboid(4.0F, -5.2222F, 5.0F, 3.0F, 12.0F, 2.0F)
                        .uv(0, 54).cuboid(-7.0F, 0.7778F, -1.0F, 14.0F, 2.0F, 2.0F),
                ModelTransform.of(0.0F, -7.2778F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData mortar = assembly.addChild("mortar", ModelPartBuilder.create(), ModelTransform.of(0f, -5.5f, 0f,0,0,0));

        mortar.addChild("barrel", ModelPartBuilder.create()
                        .uv(0, 33).cuboid(-4.0F, -21.0F, -4.0F, 8.0F, 24.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F,0,0,0));
        mortar.addChild("barrel2", ModelPartBuilder.create()
                        .uv(1, 65).cuboid(-3.0F, -36.0F, -3.0F, 6.0F, 16.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F,0,0,0));

        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0)
                .cuboid(-16.0F, -2.0F, 0.0F, 16.0F, 2.0F, 16.0F),
                ModelTransform.of(8.0F, 24.0F, -8.0F,0,0,0));

        return TexturedModelData.of(modelData, 128, 128);
    }

    public void setAngles(MortarBlockEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.barrel2.scaleY = 0.6f + (0.4f * animationProgress / entity.getPropertyDelegate().get(1));
        this.assembly.yaw = (float) ((Math.PI / 180.0) * (headYaw));
        this.mortar.pitch = (float) ((Math.PI / 180.0) * (90 - headPitch));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        assembly.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        bone.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
