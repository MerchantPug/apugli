package io.github.merchantpug.apugli.entity.feature;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.EnergySwirlOverlayPower;
import io.github.merchantpug.apugli.registry.ApugliEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PlayerEnergySwirlOverlayFeatureRenderer<T extends LivingEntity & SkinOverlayOwner> extends EnergySwirlOverlayFeatureRenderer<T, BipedEntityModel<T>> {
    private Identifier skin;
    private float speed;
    private final BipedEntityModel<T> model;

    public PlayerEnergySwirlOverlayFeatureRenderer(FeatureRendererContext<T, BipedEntityModel<T>> featureRendererContext, EntityModelLoader loader, boolean slim) {
        super(featureRendererContext);
        if (slim) {
            this.model = new BipedEntityModel<T>(loader.getModelPart(ApugliEntityModelLayers.PLAYER_SLIM_ARMOR));
        } else {
            this.model = new BipedEntityModel<T>(loader.getModelPart(ApugliEntityModelLayers.PLAYER_ARMOR));
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (PowerHolderComponent.hasPower(entity, EnergySwirlOverlayPower.class)) {
            setEnergySwirlTexture(entity);
            setSpeed(entity);
            float f = (float)entity.age + tickDelta;
            BipedEntityModel<T> entityModel = this.getContextModel();
            entityModel.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(entityModel);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(this.getEnergySwirlTexture(), this.getEnergySwirlX(f) % 1.0F, f * 0.01F % 1.0F));
            entityModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            entityModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0.5F, 0.5F, 0.5F, 1.0F);
        }
    }

    protected float getEnergySwirlX(float partialAge) {
        return partialAge * getSpeed();
    }

    protected Identifier getEnergySwirlTexture() {
        return skin;
    }

    protected float getSpeed() {
        return speed;
    }

    protected void setEnergySwirlTexture(LivingEntity entity) {
        if (PowerHolderComponent.hasPower(entity, EnergySwirlOverlayPower.class)) {
            skin = PowerHolderComponent.getPowers(entity, EnergySwirlOverlayPower.class).get(0).getTextureLocation();
        }
    }

    protected void setSpeed(LivingEntity entity) {
        if (PowerHolderComponent.hasPower(entity, EnergySwirlOverlayPower.class)) {
            speed = PowerHolderComponent.getPowers(entity, EnergySwirlOverlayPower.class).get(0).getSpeed();
        }
    }

    protected EntityModel<T> getEnergySwirlModel() {
        return this.model;
    }
}
