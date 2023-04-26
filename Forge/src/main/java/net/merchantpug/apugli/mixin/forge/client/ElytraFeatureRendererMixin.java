package net.merchantpug.apugli.mixin.forge.client;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElytraLayer.class)
public class ElytraFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(method = "shouldRender", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void allowPowerRendering(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Services.POWER.getPowers(entity, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get()).stream().anyMatch(power -> power.getStack().getItem() == Items.ELYTRA && power.getSlot() == EquipmentSlot.CHEST))
            cir.setReturnValue(true);
        if (Services.POWER.getPowers(entity, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get()).stream().anyMatch(power -> power.shouldOverride() && power.getSlot() == EquipmentSlot.CHEST))
            cir.setReturnValue(false);
    }

}
