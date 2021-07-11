package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.entity.feature.EnergySwirlOverlayFeatureRenderer;
import io.github.merchantpug.apugli.power.SetTexturePower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
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

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    @Shadow protected abstract boolean addFeature(FeatureRenderer<T, M> feature);

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void construct(EntityRendererFactory.Context ctx, M model, float shadowRadius, CallbackInfo ci) {
        this.addFeature(new EnergySwirlOverlayFeatureRenderer(this));
    }

    @ModifyVariable(method = "getRenderLayer", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier changeTexture(Identifier identifier, T entity) {
        List<SetTexturePower> changeTexturePowers = PowerHolderComponent.getPowers(entity, SetTexturePower.class);
        if (changeTexturePowers.size() > 0) {
            if (changeTexturePowers.size() > 1) {
                Apugli.LOGGER.warn("Entity " + entity.getDisplayName().toString() + " has two or more instances of SetTexturePower.");
            }
            if (changeTexturePowers.get(0).textureLocation != null) {
                return changeTexturePowers.get(0).textureLocation;
            }
        }
        return identifier;
    }
}
