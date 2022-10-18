package com.github.merchantpug.apugli.entity.renderer;

import com.github.merchantpug.apugli.platform.Services;
import com.github.merchantpug.apugli.power.EnergySwirlPower;
import com.github.merchantpug.apugli.util.TextureUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class EnergySwirlLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public EnergySwirlLayer(RenderLayerParent<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(PoseStack poses, MultiBufferSource buffer, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        float ticks = entity.tickCount + tickDelta;
        for(EnergySwirlPower power : Services.PLATFORM.getPowers(entity, EnergySwirlPower.class, )) {
            VertexConsumer consumer;
            if(power.getTextureUrl() != null) {
                TextureUtil.registerEntityTextureOverlayTexture(power.getUrlTextureIdentifier(), power.getTextureUrl());
                consumer = buffer.getBuffer(RenderType.energySwirl(power.getUrlTextureIdentifier(), this.getEnergySwirlX(ticks, power.getSpeed()) % 1.0F, ticks * 0.01F % 1.0F));
            } else if(power.getTextureLocation() != null) {
                consumer = buffer.getBuffer(RenderType.energySwirl(power.getTextureLocation(), this.getEnergySwirlX(ticks, power.getSpeed()) % 1.0F, ticks * 0.01F % 1.0F));
            } else continue;
            this.renderOverlay(consumer, poses, buffer,
                power.getSize(), light, entity,
                limbAngle, limbDistance,
                tickDelta, animationProgress,
                headYaw, headPitch
            );
        }
    }

    public void renderOverlay(VertexConsumer consumer,
                              PoseStack matrices,
                              MultiBufferSource vertexConsumers,
                              float size, int light, T entity,
                              float limbAngle, float limbDistance,
                              float tickDelta, float animationProgress,
                              float headYaw, float headPitch
    ) {
        matrices.pushPose();
        EntityModel<T> entityModel = this.getParentModel();
        entityModel.prepareMobModel(entity, limbAngle, limbDistance, tickDelta);
        this.getParentModel().copyPropertiesTo(entityModel);
        entityModel.setupAnim(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        matrices.scale(size, size, size);
        entityModel.renderToBuffer(matrices, consumer, light, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
        matrices.popPose();
    }

    protected float getEnergySwirlX(float ticks, float speed) {
        return ticks * speed;
    }
    
}