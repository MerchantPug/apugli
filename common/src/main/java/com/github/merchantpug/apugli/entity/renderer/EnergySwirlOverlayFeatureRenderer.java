package com.github.merchantpug.apugli.entity.renderer;

import com.github.merchantpug.apugli.powers.EnergySwirlPower;
import io.github.apace100.origins.component.OriginComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EnergySwirlOverlayFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    public EnergySwirlOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        OriginComponent.getPowers(entity, EnergySwirlPower.class).forEach(power -> {
            this.renderOverlay(power.getTextureLocation(), power.getSpeed(), matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        });
    }

    public void renderOverlay(Identifier textureLocation, float speed, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        float f = (float)entity.age + tickDelta;
        EntityModel<T> entityModel = this.getContextModel();
        entityModel.animateModel(entity, limbAngle, limbDistance, tickDelta);
        this.getContextModel().copyStateTo(entityModel);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(textureLocation, this.getEnergySwirlX(f, speed) % 1.0F, f * 0.01F % 1.0F));
        entityModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        entityModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0.5F, 0.5F, 0.5F, 1.0F);
    }

    protected float getEnergySwirlX(float partialAge, float speed) {
        return partialAge * speed;
    }
}