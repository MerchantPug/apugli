package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "setItemSlot", at = @At(value = "TAIL"))
    public void apugli$equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if(slot.getType() != EquipmentSlot.Type.ARMOR && !slot.equals(EquipmentSlot.OFFHAND)) return;

        Services.POWER.getPowers((Player)(Object)this, ApugliPowers.ACTION_ON_EQUIP.get()).forEach(power -> power.executeAction(slot, stack));
    }

    @Inject(method = "isModelPartShown", at = @At("RETURN"), cancellable = true)
    private void apugli$setPartsToInvisibleWithPower(PlayerModelPart modelPart, CallbackInfoReturnable<Boolean> cir) {
        if (Services.POWER.getPowers((Player)(Object)this, ApugliPowers.ENTITY_TEXTURE_OVERLAY.get()).stream().anyMatch(p -> !p.shouldRenderPlayerOuterLayer())) {
            cir.setReturnValue(false);
        }
    }

}
