package net.merchantpug.apugli.mixin.fabric.client;

import net.merchantpug.apugli.client.renderer.*;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Shadow protected abstract boolean addLayer(RenderLayer<T, M> renderLayer);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void construct(EntityRendererProvider.Context ctx, M model, float shadowRadius, CallbackInfo ci) {
        // Check PlayerEntityRendererMixin for implementation on players, that is separate due to mods that get a specific render layer.
        if (!((LivingEntityRenderer<T, M>)(Object)this instanceof PlayerRenderer)) {
            this.addLayer(new EnergySwirlLayer<>((RenderLayerParent<T, M>)this));
            this.addLayer(new EntityTextureOverlayLayer<>((RenderLayerParent<T, M>)this, false, ctx.getModelSet()));
            if (this instanceof ArmedModel && !((LivingEntityRenderer<T, M>)(Object)this instanceof HumanoidMobRenderer<?, ?>)) {
                this.addLayer(new PowerItemInHandLayer((RenderLayerParent<T, M>)this));
            }
            if (this instanceof HeadedModel && !((LivingEntityRenderer<T, M>)(Object)this instanceof HumanoidMobRenderer<?, ?>)) {
                this.addLayer(new PowerCustomHeadLayer((RenderLayerParent<T, M>)this, ctx.getModelSet()));
            }
        }
    }

}
