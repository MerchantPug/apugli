package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.SetEntityGroupPower;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.power.ModifySoulSpeedPower;
import io.github.merchantpug.apugli.power.SetApugliEntityGroupPower;
import io.github.merchantpug.apugli.registry.ApugliEntityGroups;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique private int soulSpeedEnchantmentValue;

    @Shadow public abstract EntityGroup getGroup();

    public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "shouldDisplaySoulSpeedEffects", at = @At("HEAD"), cancellable = true)
    private void shouldDisplaySoulSpeedEffects(CallbackInfoReturnable<Boolean> cir) {
        if (PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            cir.setReturnValue(this.age % 5 == 0 && this.getVelocity().x != 0.0D && this.getVelocity().z != 0.0D && !this.isSpectator() && soulSpeedValue > 0 && ((LivingEntityAccess)this).invokeIsOnSoulSpeedBlock());
        }
    }

    @ModifyVariable(method = "addSoulSpeedBoostIfNeeded", at = @At("STORE"), ordinal = 0)
    private int replaceLevelOfSouLSpeed(int i) {
        this.soulSpeedEnchantmentValue = i;
        return i = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, i);
    }

    @Inject(method = "getVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    private void getVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        if (PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            if (soulSpeedValue <= 0) {
                cir.setReturnValue(super.getVelocityMultiplier());
            }
        }
    }

    @Inject(method = "addSoulSpeedBoostIfNeeded", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void itemStack(CallbackInfo ci, int i) {
        int baseValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, 0);
        if (PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class) && i == baseValue) {
            ci.cancel();
        }
    }

    @Inject(method = "getGroup", at = @At("HEAD"), cancellable = true)
    public void getGroup(CallbackInfoReturnable<EntityGroup> cir) {
        List<SetEntityGroupPower> originsGroups = PowerHolderComponent.getPowers(this, SetEntityGroupPower.class);
        List<SetApugliEntityGroupPower> tmoGroups = PowerHolderComponent.getPowers(this, SetApugliEntityGroupPower.class);
        if(tmoGroups.size() > 0) {
            if(tmoGroups.size() > 1 || originsGroups.size() > 0) {
                Apugli.LOGGER.warn("Player " + this.getDisplayName().toString() + " has two instances of SetEntityGroupPower/SetTMOEntityGroupPower.");
            }
            cir.setReturnValue(tmoGroups.get(0).group);
        }
    }

    @Inject(method = "isUndead", at = @At("HEAD"), cancellable = true)
    private void isUndead(CallbackInfoReturnable<Boolean> cir) {
        if (this.getGroup() == ApugliEntityGroups.PLAYER_UNDEAD) {
            cir.setReturnValue(true);
        }
    }
}
