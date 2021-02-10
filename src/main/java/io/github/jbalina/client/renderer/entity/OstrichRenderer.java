package io.github.jbalina.client.renderer.entity;

import io.github.jbalina.BiomesOutvotedMod;
import io.github.jbalina.client.renderer.model.OstrichModel;
import io.github.jbalina.common.entities.OstrichEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class OstrichRenderer extends MobRenderer<OstrichEntity, OstrichModel<OstrichEntity>> {

	protected static final ResourceLocation TEXTURE = new ResourceLocation(BiomesOutvotedMod.MODID, "textures/entity/ostrich.png");
	protected static final ResourceLocation SADDLED_OSTRICH = new ResourceLocation(BiomesOutvotedMod.MODID, "textures/entity/saddled_ostrich.png");
	
	public OstrichRenderer(EntityRendererManager rendererManagerIn) {
		super(rendererManagerIn, new OstrichModel<>(), 0.25f);//3rd is shadow size
	}

	@Override
    public ResourceLocation getEntityTexture(final OstrichEntity ostrich) {
		if (ostrich.isSaddled()){
			return SADDLED_OSTRICH;
		}
		else {
			return TEXTURE;
		}
        
    }
	
	public static class RenderFactory implements IRenderFactory<OstrichEntity>
    {
        @Override
        public EntityRenderer<? super OstrichEntity> createRenderFor(EntityRendererManager manager)
        {
            return new OstrichRenderer(manager);
        }
    }

}
