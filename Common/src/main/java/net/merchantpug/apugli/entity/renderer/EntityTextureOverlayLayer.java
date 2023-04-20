package net.merchantpug.apugli.entity.renderer;

import io.github.edwinmindcraft.apoli.common.power.ModelColorPower;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.TextureUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class EntityTextureOverlayLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    @Nullable
    private PlayerModel<T> extraPlayerModel;

    public EntityTextureOverlayLayer(RenderLayerParent<T, M> context, boolean slim, EntityModelSet loader) {
        super(context);
        if (context.getModel() instanceof PlayerModel<?>) {
            extraPlayerModel = new PlayerModel<>(loader.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim);
        }
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.isInvisible() && !Minecraft.getInstance().shouldEntityAppearGlowing(entity)) return;

        Services.POWER.getPowers(entity, ApugliPowers.ENTITY_TEXTURE_OVERLAY.get()).forEach(power -> {
            if (power.getTextureLocation() == null && power.getTextureUrl() == null) return;

            RenderType renderLayer = null;
            if (TextureUtil.getPowerIdToUrl().containsKey(power.getType().getIdentifier())) {
                renderLayer = Minecraft.getInstance().shouldEntityAppearGlowing(entity) && entity.isInvisible() ? RenderType.outline(power.getUrlTextureIdentifier()) : RenderType.entityTranslucent(power.getUrlTextureIdentifier());
            } else if (power.getTextureLocation() != null) {
                renderLayer = Minecraft.getInstance().shouldEntityAppearGlowing(entity) && entity.isInvisible() ? RenderType.outline(power.getTextureLocation()) : RenderType.entityTranslucent(power.getTextureLocation());
            }

            if (renderLayer != null) {
                matrices.pushPose();
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

                EntityModel<T> entityModel = extraPlayerModel != null ? extraPlayerModel : this.getParentModel();

                if (this.getParentModel() instanceof PlayerModel<?> originalModel) {
                    PlayerModel<T> extraModel = (PlayerModel<T>) entityModel;
                    ((PlayerModel<T>) originalModel).copyPropertiesTo(extraModel);
                    extraModel.leftSleeve.copyFrom(originalModel.leftSleeve);
                    extraModel.rightSleeve.copyFrom(originalModel.rightSleeve);
                    extraModel.leftPants.copyFrom(originalModel.leftPants);
                    extraModel.rightPants.copyFrom(originalModel.rightPants);
                }

                entityModel.renderToBuffer(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
                matrices.popPose();
            }
        });
    }
}
