package net.merchantpug.apugli.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.merchantpug.apugli.client.util.TextureUtilClient;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.EnergySwirlPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
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
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        Services.POWER.getPowers(entity, ApugliPowers.ENERGY_SWIRL.get()).forEach(power -> {
            this.renderOverlay(power, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        });
    }

    public void renderOverlay(EnergySwirlPower power, PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (power.getTextureLocation() == null && power.getTextureUrl() == null) return;

        float f = (float)entity.tickCount + tickDelta;

        VertexConsumer vertexConsumer = null;
        if (TextureUtilClient.getUrls().containsKey(power.getUrlTextureIdentifier())) {
            vertexConsumer = vertexConsumers.getBuffer(RenderType.energySwirl(power.getUrlTextureIdentifier(), this.getEnergySwirlX(f, power.getSpeed()) % 1.0F, f * 0.01F % 1.0F));
        } else if (power.getTextureLocation() != null) {
            vertexConsumer = vertexConsumers.getBuffer(RenderType.energySwirl(power.getTextureLocation(), this.getEnergySwirlX(f, power.getSpeed()) % 1.0F, f * 0.01F % 1.0F));
        }

        if (vertexConsumer != null) {
            matrices.pushPose();
            EntityModel<T> entityModel = this.getParentModel();
            entityModel.prepareMobModel(entity, limbAngle, limbDistance, tickDelta);
            this.getParentModel().copyPropertiesTo(entityModel);

            entityModel.setupAnim(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            matrices.scale(power.getSize(), power.getSize(), power.getSize());
            entityModel.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
            matrices.popPose();
        }
    }

    protected float getEnergySwirlX(float partialAge, float speed) {
        return partialAge * speed;
    }
}