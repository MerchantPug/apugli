package net.merchantpug.apugli.mixin.fabric.client;

import net.merchantpug.apugli.client.renderer.*;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerEntityRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void apugli$addLayersPlayer(EntityRendererProvider.Context ctx, boolean slim, CallbackInfo ci) {
        // This is separated from LivingEntityRendererMixin as it breaks certain mods if it's part of that.
        this.addLayer(new EnergySwirlLayer<>(this));
        this.addLayer(new EntityTextureOverlayLayer<>(this, slim, ctx.getModelSet()));
        this.addLayer(new PowerItemInHandLayer<>(this, ctx.getItemInHandRenderer()));
        this.addLayer(new PowerCustomHeadLayer<>(this, ctx.getModelSet(), ctx.getItemInHandRenderer()));
        this.addLayer(new PowerHumanoidArmorLayer<>(this, this.getModel(), this.getModel(), ctx.getModelManager()));
    }

}
