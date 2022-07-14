package com.github.merchantpug.apugli.mixin.client;

import io.github.apace100.origins.component.OriginComponent;
import com.github.merchantpug.apugli.powers.ModifyEquippedItemRenderPower;
import com.github.merchantpug.apugli.util.ModifyEquippedItemRenderUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    @Shadow protected abstract A getArmor(EquipmentSlot equipmentSlot);

    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"))
    private void renderModified(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = OriginComponent.getPowers(livingEntity, ModifyEquippedItemRenderPower.class);
        modifyEquippedItemRenderPowers.forEach(power -> {
            if (power.slot.getType() == EquipmentSlot.Type.ARMOR) {
                ModifyEquippedItemRenderUtil.renderArmor((ArmorFeatureRenderer<?, ?, ?>)(Object)this, power.stack, matrixStack, vertexConsumerProvider, livingEntity, power.slot, i, this.getArmor(power.slot));
            }
        });
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private void preventArmorRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = OriginComponent.getPowers(entity, ModifyEquippedItemRenderPower.class);
        if (modifyEquippedItemRenderPowers.stream().anyMatch(power -> power.shouldOverride() && power.slot == armorSlot)) ci.cancel();
    }
}