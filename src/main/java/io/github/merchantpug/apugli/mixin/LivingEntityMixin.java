package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.SetEntityGroupPower;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.power.ExtraSoulSpeedPower;
import io.github.merchantpug.apugli.power.SetApugliEntityGroupPower;
import io.github.merchantpug.apugli.power.UnenchantedSoulSpeedPower;
import io.github.merchantpug.apugli.registry.ApugliEntityGroups;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract EntityGroup getGroup();

    public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "shouldDisplaySoulSpeedEffects", at = @At("HEAD"), cancellable = true)
    private void shouldDisplaySoulSpeedEffects(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.age % 5 == 0 && this.getVelocity().x != 0.0D && this.getVelocity().z != 0.0D && !this.isSpectator() && (EnchantmentHelper.hasSoulSpeed((LivingEntity) (Object) this) || PowerHolderComponent.hasPower(this, UnenchantedSoulSpeedPower.class) || PowerHolderComponent.hasPower(this, ExtraSoulSpeedPower.class)) && ((LivingEntityAccess)this).invokeIsOnSoulSpeedBlock());
    }

    @ModifyVariable(method = "addSoulSpeedBoostIfNeeded", at = @At("STORE"), ordinal = 0)
    private int replaceLevelOfSouLSpeed(int i) {
        if (PowerHolderComponent.hasPower(this, UnenchantedSoulSpeedPower.class) && i <= PowerHolderComponent.getPowers(this, UnenchantedSoulSpeedPower.class).get(0).getModifier()) {
            return i = (PowerHolderComponent.getPowers(this, UnenchantedSoulSpeedPower.class).get(0).getModifier());
        }
        if (PowerHolderComponent.hasPower(this, ExtraSoulSpeedPower.class)) {
            return i += (PowerHolderComponent.getPowers(this, ExtraSoulSpeedPower.class).get(0).getModifier());
        }
        return i;
    }

    @Inject(method = "addSoulSpeedBoostIfNeeded", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void itemStack(CallbackInfo ci, int i) {
        if (PowerHolderComponent.hasPower(this, UnenchantedSoulSpeedPower.class) && i <= (PowerHolderComponent.getPowers(this, UnenchantedSoulSpeedPower.class).get(0).getModifier())) {
            ci.cancel();
        }
        if (PowerHolderComponent.hasPower(this, ExtraSoulSpeedPower.class) && i == (PowerHolderComponent.getPowers(this, ExtraSoulSpeedPower.class).get(0).getModifier())) {
            ci.cancel();
        }
    }

    @Inject(method = "getGroup", at = @At("HEAD"), cancellable = true)
    public void getGroup(CallbackInfoReturnable<EntityGroup> cir) {
        if((Object)this instanceof PlayerEntity) {
            List<SetEntityGroupPower> originsGroups = PowerHolderComponent.getPowers(this, SetEntityGroupPower.class);
            List<SetApugliEntityGroupPower> tmoGroups = PowerHolderComponent.getPowers(this, SetApugliEntityGroupPower.class);
            if(tmoGroups.size() > 0) {
                if(tmoGroups.size() > 1 || tmoGroups.size() > 0 && originsGroups.size() > 0) {
                    Apugli.LOGGER.warn("Player " + this.getDisplayName().toString() + " has two instances of SetEntityGroupPower/SetTMOEntityGroupPower.");
                }
                cir.setReturnValue(tmoGroups.get(0).group);
            }
        }
    }

    @Inject(method = "isUndead", at = @At("HEAD"), cancellable = true)
    private void isUndead(CallbackInfoReturnable<Boolean> cir) {
        if (this.getGroup() == ApugliEntityGroups.PLAYER_UNDEAD) {
            cir.setReturnValue(true);
        }
    }
}
