package io.github.merchantpug.apugli.mixin;

import io.github.merchantpug.apugli.entity.feature.StackHelmetFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ZombieBaseEntityRenderer.class)
public class ZombieBaseEntityRendererMixin<T extends ZombieEntity, M extends ZombieEntityModel<T>> extends BipedEntityRenderer<T, M> {

    public ZombieBaseEntityRendererMixin(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void construct(EntityRendererFactory.Context ctx, M bodyModel, M legsArmorModel, M bodyArmorModel, CallbackInfo ci) {
        this.addFeature(new StackHelmetFeatureRenderer(this, bodyArmorModel));
    }
}
