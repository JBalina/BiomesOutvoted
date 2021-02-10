package io.github.jbalina.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import io.github.jbalina.common.entities.OstrichEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class OstrichModel<T extends OstrichEntity> extends EntityModel<T> {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer right_leg;
	private final ModelRenderer left_leg;
	private final ModelRenderer neck;
	private final ModelRenderer tail;

	public OstrichModel() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.5F, 8.75F, -9.0F);
		head.setTextureOffset(15, 39).addBox(-2.5F, -2.25F, -5.0F, 5.0F, 5.0F, 5.0F, 0.0F, false);
		head.setTextureOffset(20, 23).addBox(-2.5F, -8.25F, -9.0F, 5.0F, 11.0F, 5.0F, 0.0F, false);
		head.setTextureOffset(0, 23).addBox(-2.5F, -17.25F, -6.0F, 5.0F, 12.0F, 5.0F, 0.0F, false);
		head.setTextureOffset(35, 23).addBox(-2.5F, -15.25F, -9.0F, 5.0F, 2.0F, 3.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setRotationPoint(0.5F, 8.0F, 1.5F);
		body.setTextureOffset(0, 0).addBox(-6.5F, -5.0F, -6.5F, 13.0F, 10.0F, 13.0F, 0.0F, false);

		right_leg = new ModelRenderer(this);
		right_leg.setRotationPoint(2.0F, 13.0F, 2.0F);
		right_leg.setTextureOffset(0, 40).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 11.0F, 2.0F, 0.0F, false);

		left_leg = new ModelRenderer(this);
		left_leg.setRotationPoint(-1.0F, 13.0F, 2.0F);
		left_leg.setTextureOffset(0, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 11.0F, 2.0F, 0.0F, false);

		neck = new ModelRenderer(this);
		neck.setRotationPoint(0.5F, 9.0F, -5.0F);
		neck.setTextureOffset(36, 36).addBox(-4.5F, -3.0F, -4.0F, 9.0F, 6.0F, 4.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, 8.0F, 8.0F);
		tail.setTextureOffset(39, 0).addBox(-3.0F, 0.0F, 0.0F, 7.0F, 6.0F, 4.0F, 0.0F, false);
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		head.render(matrixStack, buffer, packedLight, packedOverlay);
		body.render(matrixStack, buffer, packedLight, packedOverlay);
		right_leg.render(matrixStack, buffer, packedLight, packedOverlay);
		left_leg.render(matrixStack, buffer, packedLight, packedOverlay);
		neck.render(matrixStack, buffer, packedLight, packedOverlay);
		tail.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.rotateAngleX = headPitch / (180F / (float)Math.PI);
	    this.head.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
	    this.right_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
	    this.left_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		
	}
}