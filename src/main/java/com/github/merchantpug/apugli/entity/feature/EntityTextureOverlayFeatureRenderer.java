package com.github.merchantpug.apugli.entity.feature;

import com.github.merchantpug.apugli.util.TextureUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import com.github.merchantpug.apugli.power.EntityTextureOverlayPower;
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
        PowerHolderComponent.getPowers(entity, EntityTextureOverlayPower.class).forEach(power -> {
            if (power.textureUrl != null) {
                TextureUtil.registerEntityTextureOverlayTexture(power.getUrlTextureIdentifier(), power.textureUrl);
                entityModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(power.getUrlTextureIdentifier())), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            } else if (power.textureLocation != null) {
                entityModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(power.textureLocation)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        });
        matrices.pop();
    }
}
