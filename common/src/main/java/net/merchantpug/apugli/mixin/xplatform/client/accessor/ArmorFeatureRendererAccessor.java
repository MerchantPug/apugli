package net.merchantpug.apugli.mixin.xplatform.client.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HumanoidArmorLayer.class)
public interface ArmorFeatureRendererAccessor<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
    @Invoker("setPartVisibility")
    void apugli$invokeSetVisible(A bipedModel, EquipmentSlot slot);

    @Invoker("renderModel")
    void apugli$invokeRenderArmorParts(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ArmorItem item, A model, boolean legs, float red, float green, float blue, @Nullable String overlay);

    @Invoker("renderTrim")
    void apugli$invokeRenderTrim(ArmorMaterial armorMaterial, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorTrim armorTrim, A humanoidModel, boolean bl);

    @Invoker("renderGlint")
    void apugli$invokeRenderGlint(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, A humanoidModel);

    @Invoker("getArmorModel")
    A apugli$invokeGetArmorModel(EquipmentSlot slot);

}
