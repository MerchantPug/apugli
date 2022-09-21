package net.merchantpug.apugli.entity.feature;

import net.merchantpug.apugli.util.TextureUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.power.EnergySwirlPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class EnergySwirlOverlayFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    public EnergySwirlOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        PowerHolderComponent.getPowers(entity, EnergySwirlPower.class).forEach(power -> {
            this.renderOverlay(power, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        });
    }

    public void renderOverlay(EnergySwirlPower power, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        float f = (float)entity.age + tickDelta;

        VertexConsumer vertexConsumer = null;
        if (power.getTextureUrl() != null) {
            TextureUtil.registerEntityTextureOverlayTexture(power.getUrlTextureIdentifier(), power.getTextureUrl());
            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(power.getUrlTextureIdentifier(), this.getEnergySwirlX(f, power.getSpeed()) % 1.0F, f * 0.01F % 1.0F));
        } else if (power.getTextureLocation() != null) {
            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(power.getTextureLocation(), this.getEnergySwirlX(f, power.getSpeed()) % 1.0F, f * 0.01F % 1.0F));
        }

        if (vertexConsumer != null) {
            matrices.push();
            EntityModel<T> entityModel = this.getContextModel();
            entityModel.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(entityModel);

            entityModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            matrices.scale(power.getSize(), power.getSize(), power.getSize());
            entityModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0.5F, 0.5F, 0.5F, 1.0F);
            matrices.pop();
        }
    }

    protected float getEnergySwirlX(float partialAge, float speed) {
        return partialAge * speed;
    }
}