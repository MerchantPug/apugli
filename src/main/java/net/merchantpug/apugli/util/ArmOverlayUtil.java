package net.merchantpug.apugli.util;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ModelColorPower;
import net.merchantpug.apugli.power.EntityTextureOverlayPower;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class ArmOverlayUtil {
    public static void renderArmOverlay(EntityTextureOverlayPower power, AbstractClientPlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ModelPart arm, ModelPart sleeve) {
        if (!power.shouldShowFirstPerson()) return;
        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;
        float alpha = 1.0F;

        if (power.shouldUseRenderingPowers()) {
            List<ModelColorPower> modelColorPowers = PowerHolderComponent.getPowers(player, ModelColorPower.class);
            if (modelColorPowers.size() > 0) {
                red = modelColorPowers.stream().map(ModelColorPower::getRed).reduce((a, b) -> a * b).get();
                green = modelColorPowers.stream().map(ModelColorPower::getGreen).reduce((a, b) -> a * b).get();
                blue = modelColorPowers.stream().map(ModelColorPower::getBlue).reduce((a, b) -> a * b).get();
                alpha = modelColorPowers.stream().map(ModelColorPower::getAlpha).min(Float::compare).get();
            }
        }
        RenderLayer renderLayer;
        if (power.getTextureUrl() != null) {
            TextureUtil.registerEntityTextureOverlayTexture(power.getUrlTextureIdentifier(), power.getTextureUrl());

            renderLayer = alpha == 1.0 ? RenderLayer.getEntityCutoutNoCull(power.getUrlTextureIdentifier()) : RenderLayer.getEntityTranslucent(power.getUrlTextureIdentifier());

            arm.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
            sleeve.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
        } else if (power.getTextureLocation() != null) {
            renderLayer = alpha == 1.0 ? RenderLayer.getEntityCutoutNoCull(power.getTextureLocation()) : RenderLayer.getEntityTranslucent(power.getTextureLocation());

            arm.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
            sleeve.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
        }
    }
}