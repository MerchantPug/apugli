package net.merchantpug.apugli.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.merchantpug.apugli.mixin.xplatform.client.accessor.FeatureRendererAccessor;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PowerItemInHandLayer<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends ItemInHandLayer<T, M> {

    public PowerItemInHandLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer renderer) {
        super(parent, renderer);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        boolean bl = entity.getMainArm() == HumanoidArm.RIGHT;

        Services.POWER.getPowers(entity, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get()).forEach(p -> {
            ItemStack leftHandStack = ItemStack.EMPTY;
            ItemStack rightHandStack = ItemStack.EMPTY;
            if(bl) {
                if(p.getSlot() == EquipmentSlot.OFFHAND) {
                    leftHandStack = p.getStack();
                }
                if(p.getSlot() == EquipmentSlot.MAINHAND) {
                    rightHandStack = p.getStack();
                }
            } else {
                if(p.getSlot() == EquipmentSlot.MAINHAND) {
                    leftHandStack = p.getStack();
                }
                if(p.getSlot() == EquipmentSlot.OFFHAND) {
                    rightHandStack = p.getStack();
                }
            }
            if(!leftHandStack.isEmpty() || !rightHandStack.isEmpty()) {
                matrices.pushPose();
                if(((FeatureRendererAccessor)this).apugli$getRenderer().getModel().young) {
                    matrices.translate(0.0D, 0.75D, 0.0D);
                    matrices.scale(0.5F, 0.5F, 0.5F);
                }

                renderArmWithItem(entity, rightHandStack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, matrices, vertexConsumers, light);
                renderArmWithItem(entity, leftHandStack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, matrices, vertexConsumers, light);
                matrices.popPose();
            }
        });
    }

}
