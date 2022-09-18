package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.entity.feature.EnergySwirlOverlayFeatureRenderer;
import com.github.merchantpug.apugli.entity.feature.EntityTextureOverlayFeatureRenderer;
import com.github.merchantpug.apugli.power.SetTexturePower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
    @Shadow protected abstract boolean addFeature(FeatureRenderer<T, M> feature);

    @Shadow public abstract M getModel();

    @Shadow protected M model;

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addFeatures(EntityRendererFactory.Context ctx, M model, float shadowRadius, CallbackInfo ci) {
        // Check PlayerEntityRendererMixin for implementation on players, that is separate due to mods that get a specific render layer.
        if ((LivingEntityRenderer<T, M>)(Object)this instanceof FeatureRendererContext<T, M> && !((LivingEntityRenderer<T, M>)(Object)this instanceof PlayerEntityRenderer)) {
            this.addFeature(new EnergySwirlOverlayFeatureRenderer<>((FeatureRendererContext<T, M>)this));
            this.addFeature(new EntityTextureOverlayFeatureRenderer<>((FeatureRendererContext<T, M>)this));
        }
    }

    @ModifyVariable(method = "getRenderLayer", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier changeTexture(Identifier identifier, T entity) {
        if (PowerHolderComponent.hasPower(entity, SetTexturePower.class)) {
            SetTexturePower texturePower = PowerHolderComponent.getPowers(entity, SetTexturePower.class).get(0);
            if (texturePower.textureLocation != null) {
                return texturePower.textureLocation;
            }
        }
        return identifier;
    }
}
