package net.merchantpug.apugli.mixin.xplatform.client;

import the.great.migration.merchantpug.apugli.entity.feature.EnergySwirlOverlayFeatureRenderer;
import the.great.migration.merchantpug.apugli.entity.feature.EntityTextureOverlayFeatureRenderer;
import the.great.migration.merchantpug.apugli.power.SetTexturePower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    @Shadow protected abstract boolean addFeature(RenderLayer<T, M> feature);

    @Shadow public abstract M getModel();

    @Shadow protected M model;

    protected LivingEntityRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void construct(EntityRendererProvider.Context ctx, M model, float shadowRadius, CallbackInfo ci) {
        this.addFeature(new EnergySwirlOverlayFeatureRenderer<>(this));
        this.addFeature(new EntityTextureOverlayFeatureRenderer<>(this));
    }

    @ModifyVariable(method = "getRenderLayer", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private ResourceLocation changeTexture(ResourceLocation identifier, T entity) {
        if(PowerHolderComponent.hasPower(entity, SetTexturePower.class)) {
            SetTexturePower texturePower = PowerHolderComponent.getPowers(entity, SetTexturePower.class).get(0);
            if(texturePower.textureLocation != null) {
                return texturePower.textureLocation;
            }
        }
        return identifier;
    }
}
