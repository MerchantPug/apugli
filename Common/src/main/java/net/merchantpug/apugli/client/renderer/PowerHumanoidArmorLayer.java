package net.merchantpug.apugli.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.merchantpug.apugli.mixin.xplatform.client.accessor.ArmorFeatureRendererAccessor;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PowerHumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, A> {

    public PowerHumanoidArmorLayer(RenderLayerParent<T, M> parent, A innerModel, A outerModel) {
        super(parent, innerModel, outerModel);
    }

    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = Services.POWER.getPowers(entity, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get());
        modifyEquippedItemRenderPowers.forEach(power -> {
            if(power.getSlot().getType() == EquipmentSlot.Type.ARMOR) {
                renderArmor(power.getStack(), matrices, vertexConsumers, power.getSlot(), light, ((ArmorFeatureRendererAccessor)this).invokeGetArmorModel(power.getSlot()));
            }
        });
    }

    public void renderArmor(ItemStack stack, PoseStack matrices, MultiBufferSource vertexConsumers, EquipmentSlot armorSlot, int light, HumanoidModel model) {
        if(stack.getItem() instanceof ArmorItem armorItem) {
            if(armorItem.getSlot() == armorSlot) {
                this.getParentModel().copyPropertiesTo(model);
                ((ArmorFeatureRendererAccessor)this).invokeSetVisible(model, armorSlot);
                boolean bl = armorSlot == EquipmentSlot.LEGS;
                boolean bl2 = stack.hasFoil();
                if(armorItem instanceof DyeableArmorItem) {
                    int i = ((DyeableArmorItem) armorItem).getColor(stack);
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float g = (float) (i >> 8 & 255) / 255.0F;
                    float h = (float) (i & 255) / 255.0F;
                    ((ArmorFeatureRendererAccessor)this).invokeRenderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, f, g, h, null);
                    ((ArmorFeatureRendererAccessor)this).invokeRenderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0F, 1.0F, 1.0F, "overlay");
                } else {
                    ((ArmorFeatureRendererAccessor)this).invokeRenderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0F, 1.0F, 1.0F, null);
                }
            }
        }
    }

}
