package com.github.merchantpug.apugli.entity.renderer;

import com.github.merchantpug.apugli.platform.Services;
import com.github.merchantpug.apugli.power.EntityTextureOverlayPower;
import com.github.merchantpug.apugli.util.TextureUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class EntityTextureOverlayLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public EntityTextureOverlayLayer(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Override
    public void render(PoseStack poses, MultiBufferSource buffer, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if(entity.isInvisible() && !Minecraft.getInstance().shouldEntityAppearGlowing(entity)) return;
        EntityModel<T> entityModel = this.getParentModel();
        List<EntityTextureOverlayPower> overlayPowers = Services.PLATFORM.getPowers(entity, EntityTextureOverlayPower.class, );
        if(overlayPowers.isEmpty()) return;
        float r = 1, g = 1, b = 1, a = 1;
        List<ModelColorPower> modelColorPowers = PowerHolderComponent.getPowers(entity, ModelColorPower.class);
        if(modelColorPowers.size() > 0) {
            r = modelColorPowers.stream().map(ModelColorPower::getRed).reduce((a, b) -> a * b).get();
            g = modelColorPowers.stream().map(ModelColorPower::getGreen).reduce((a, b) -> a * b).get();
            b = modelColorPowers.stream().map(ModelColorPower::getBlue).reduce((a, c) -> a * c).get();
            a = modelColorPowers.stream().map(ModelColorPower::getAlpha).min(Float::compare).get();
        }
        for(EntityTextureOverlayPower power : overlayPowers) {
            if(power.textureLocation == null && power.textureUrl == null) {
                return;
            }
            RenderType renderLayer;
            if(power.textureUrl != null) {
                TextureUtil.registerEntityTextureOverlayTexture(power.getUrlTextureIdentifier(), power.textureUrl);
                renderLayer = Minecraft.getInstance().shouldEntityAppearGlowing(entity) && entity.isInvisible() ?
                    RenderType.outline(power.getUrlTextureIdentifier()) :
                    RenderType.entityTranslucent(power.getUrlTextureIdentifier());
            } else {
                renderLayer = Minecraft.getInstance().shouldEntityAppearGlowing(entity) && entity.isInvisible() ?
                    RenderType.outline(power.textureLocation) :
                    RenderType.entityTranslucent(power.textureLocation);
            }
            poses.pushPose();
            if(power.usesRenderingPowers) {
                entityModel.renderToBuffer(poses, buffer.getBuffer(renderLayer), light, OverlayTexture.NO_OVERLAY, r, g, b, a);
            } else {
                entityModel.renderToBuffer(poses, buffer.getBuffer(renderLayer), light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            }
            poses.popPose();
        }
    }
    
}