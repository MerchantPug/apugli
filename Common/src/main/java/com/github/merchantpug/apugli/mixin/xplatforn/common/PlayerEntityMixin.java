package com.github.merchantpug.apugli.mixin.xplatforn.common;

import the.great.migration.merchantpug.apugli.power.ActionOnEquipPower;
import the.great.migration.merchantpug.apugli.power.AerialAffinityPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Redirect(method = "getBlockBreakingSpeed", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;onGround:Z", opcode = Opcodes.GETFIELD))
    private boolean hasAirAffinity(Player instance) {
        return PowerHolderComponent.hasPower(instance, AerialAffinityPower.class) || instance.isOnGround();
    }

    @Inject(method = "equipStack", at = @At(value = "TAIL"))
    public void equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if(slot.getType() != EquipmentSlot.Type.ARMOR && !slot.equals(EquipmentSlot.OFFHAND)) return;

        PowerHolderComponent.getPowers((Player)(Object)this, ActionOnEquipPower.class).forEach(power -> power.fireAction(slot, stack));
    }
}
