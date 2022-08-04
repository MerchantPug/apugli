package com.github.merchantpug.apugli.entity.feature;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.util.TextureUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import com.github.merchantpug.apugli.power.EntityTextureOverlayPower;
import io.github.apace100.apoli.power.ModelColorPower;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class EntityTextureOverlayFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    public EntityTextureOverlayFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.isInvisible() && !MinecraftClient.getInstance().hasOutline(entity)) return;
        EntityModel<T> entityModel = this.getContextModel();
        PowerHolderComponent.getPowers(entity, EntityTextureOverlayPower.class).forEach(power -> {
            if (power.textureLocation == null && power.textureUrl == null) {
                return;
            }

            RenderLayer renderLayer;
            if (power.textureUrl != null) {
                TextureUtil.registerEntityTextureOverlayTexture(power.getUrlTextureIdentifier(), power.textureUrl);
                renderLayer = MinecraftClient.getInstance().hasOutline(entity) && entity.isInvisible() ? RenderLayer.getOutline(power.getUrlTextureIdentifier()) : RenderLayer.getEntityTranslucent(power.getUrlTextureIdentifier());
            } else {
                renderLayer = MinecraftClient.getInstance().hasOutline(entity) && entity.isInvisible() ? RenderLayer.getOutline(power.textureLocation) : RenderLayer.getEntityTranslucent(power.textureLocation);
            }

            if (renderLayer != null) {
                matrices.push();
                float red = 1.0F;
                float green = 1.0F;
                float blue = 1.0F;
                float alpha = 1.0F;

                if (power.usesRenderingPowers) {
                    List<ModelColorPower> modelColorPowers = PowerHolderComponent.getPowers(entity, ModelColorPower.class);
                    if (modelColorPowers.size() > 0) {
                        red = modelColorPowers.stream().map(ModelColorPower::getRed).reduce((a, b) -> a * b).get();
                        green = modelColorPowers.stream().map(ModelColorPower::getGreen).reduce((a, b) -> a * b).get();
                        blue = modelColorPowers.stream().map(ModelColorPower::getBlue).reduce((a, c) -> a * c).get();
                        alpha = modelColorPowers.stream().map(ModelColorPower::getAlpha).min(Float::compare).get();
                    }
                }

                entityModel.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
                matrices.pop();
            }
        });
    }
}
