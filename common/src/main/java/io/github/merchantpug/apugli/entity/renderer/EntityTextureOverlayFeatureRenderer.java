package io.github.merchantpug.apugli.entity.renderer;

import io.github.apace100.origins.component.OriginComponent;
import io.github.merchantpug.apugli.powers.EntityTextureOverlayPower;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class EntityTextureOverlayFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    public EntityTextureOverlayFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        matrices.push();
        EntityModel<T> entityModel = this.getContextModel();
        OriginComponent.getPowers(entity, EntityTextureOverlayPower.class).forEach(power -> {
            entityModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(power.textureLocation)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        });
        matrices.pop();
    }
}
