package net.merchantpug.apugli.entity.feature;

import net.merchantpug.apugli.util.TextureUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.power.EntityTextureOverlayPower;
import io.github.apace100.apoli.power.ModelColorPower;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityTextureOverlayFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    @Nullable private PlayerEntityModel<T> extraPlayerModel;

    public EntityTextureOverlayFeatureRenderer(FeatureRendererContext<T, M> context, boolean slim, EntityModelLoader loader) {
        super(context);
        if (context.getModel() instanceof PlayerEntityModel<?>) {
            extraPlayerModel = new PlayerEntityModel<>(loader.getModelPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim);
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.isInvisible() && !MinecraftClient.getInstance().hasOutline(entity)) return;
        if (extraPlayerModel != null) {
            this.getContextModel().copyStateTo(extraPlayerModel);
            extraPlayerModel.animateModel(entity, limbAngle, limbDistance, tickDelta);
            extraPlayerModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        }
        EntityModel<T> entityModel = extraPlayerModel != null ? extraPlayerModel : this.getContextModel();

        PowerHolderComponent.getPowers(entity, EntityTextureOverlayPower.class).forEach(power -> {
            if (power.getTextureLocation() == null && power.getTextureUrl() == null) {
                return;
            }

            RenderLayer renderLayer = null;
            if (TextureUtil.getPowerIdToUrl().containsKey(power.getType().getIdentifier())) {
                renderLayer = MinecraftClient.getInstance().hasOutline(entity) && entity.isInvisible() ? RenderLayer.getOutline(power.getUrlTextureIdentifier()) : RenderLayer.getEntityTranslucent(power.getUrlTextureIdentifier());
            } else if (power.getTextureLocation() != null) {
                renderLayer = MinecraftClient.getInstance().hasOutline(entity) && entity.isInvisible() ? RenderLayer.getOutline(power.getTextureLocation()) : RenderLayer.getEntityTranslucent(power.getTextureLocation());
            }

            if (renderLayer != null) {
                matrices.push();
                float red = 1.0F;
                float green = 1.0F;
                float blue = 1.0F;
                float alpha = 1.0F;

                if (power.shouldUseRenderingPowers()) {
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
