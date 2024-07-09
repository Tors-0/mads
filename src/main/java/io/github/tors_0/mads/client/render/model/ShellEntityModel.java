package io.github.tors_0.mads.client.render.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.entity.ShellEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

// Made with Blockbench 4.10.0
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class ShellEntityModel<T extends ShellEntity> extends EntityModel<T> {
	public static final Identifier IDENTIFIER = Mads.getId("textures/entity/shell.png");
	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(IDENTIFIER, "main");
	private final ModelPart bone;

	public ShellEntityModel(ModelPart root) {
		this.bone = root.getChild("bone");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(12, 0).cuboid(-2.0F, -6.0F, -2.0F, 4.0F, 8.0F, 4.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-1.5F, -10.0F, -1.5F, 3.0F, 13.0F, 3.0F, new Dilation(0.0F))
				.uv(12, 12).cuboid(-1.0F, -12.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
				.uv(9, 0).cuboid(-0.5F, -13.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 21.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void setAngles(ShellEntity entity, float limbSwing, float limbSwingAmount,
						  float ageInTicks, float netHeadYaw, float headPitch) {
		bone.yaw=0;
		bone.pitch=0;
		bone.roll=0;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}