package io.github.merchantpug.apugli.entity.feature;

import com.google.common.collect.Maps;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.WearableItemStackPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class StackHelmetFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
    private final A bodyModel;
    private ItemStack stack;

    public StackHelmetFeatureRenderer(FeatureRendererContext<T, M> context, A bodyModel) {
        super(context);
        this.bodyModel = bodyModel;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        setStack(livingEntity);
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, i, this.bodyModel);
    }

    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, int light, A model) {
        if (PowerHolderComponent.hasPower(entity, WearableItemStackPower.class)) {
            ItemStack itemStack = this.stack;
            if (itemStack.getItem() instanceof ArmorItem armorItem) {
                if (armorItem.getSlotType() == EquipmentSlot.HEAD) {
                    this.getContextModel().setAttributes(model);
                    this.setVisible(model);
                    boolean bl2 = itemStack.hasGlint();
                    if (armorItem instanceof DyeableArmorItem) {
                        int i = ((DyeableArmorItem) armorItem).getColor(itemStack);
                        float f = (float) (i >> 16 & 255) / 255.0F;
                        float g = (float) (i >> 8 & 255) / 255.0F;
                        float h = (float) (i & 255) / 255.0F;
                        this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, f, g, h, null);
                        this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, 1.0F, 1.0F, 1.0F, "overlay");
                    } else {
                        this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, 1.0F, 1.0F, 1.0F, null);
                    }
                }
            }
        }
    }

    protected void setVisible(A bipedModel) {
        bipedModel.setVisible(false);
        bipedModel.head.visible = true;
        bipedModel.hat.visible = true;
    }

    private void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean usesSecondLayer, A model, float red, float green, float blue, @Nullable String overlay) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(item, overlay)), false, usesSecondLayer);
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
    }

    private Identifier getArmorTexture(ArmorItem item, @Nullable String overlay) {
        String var10000 = item.getMaterial().getName();
        String string = "textures/models/armor/" + var10000 + "_layer_" + 1 + (overlay == null ? "" : "_" + overlay) + ".png";
        return ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
    }

    protected void setStack(LivingEntity entity) {
        if (PowerHolderComponent.hasPower(entity, WearableItemStackPower.class)) {
            WearableItemStackPower power = PowerHolderComponent.getPowers(entity, WearableItemStackPower.class).get(0);
            stack = power.getItemStack() != null ? power.getItemStack() : ItemStack.EMPTY;
        } else {
            stack = ItemStack.EMPTY;
        }
    }
}
