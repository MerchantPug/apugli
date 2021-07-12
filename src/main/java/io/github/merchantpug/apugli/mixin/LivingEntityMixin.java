package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.SetEntityGroupPower;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.access.LivingEntityAccess;
import io.github.merchantpug.apugli.power.*;
import io.github.merchantpug.apugli.registry.ApugliEntityGroups;
import io.github.merchantpug.nibbles.ItemStackFoodComponentAPI;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
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
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccess {

    @Shadow public abstract EntityGroup getGroup();

    @Shadow public abstract boolean isFallFlying();

    @Unique private int apugli_amountOfEdiblePower = 0;

    public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "shouldDisplaySoulSpeedEffects", at = @At("HEAD"), cancellable = true)
    private void shouldDisplaySoulSpeedEffects(CallbackInfoReturnable<Boolean> cir) {
        if (PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            cir.setReturnValue(this.age % 5 == 0 && this.getVelocity().x != 0.0D && this.getVelocity().z != 0.0D && !this.isSpectator() && soulSpeedValue > 0 && ((LivingEntityAccessor)this).invokeIsOnSoulSpeedBlock());
        }
    }

    @ModifyVariable(method = "addSoulSpeedBoostIfNeeded", at = @At("STORE"), ordinal = 0)
    private int replaceLevelOfSouLSpeed(int i) {
        return i = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, i);
    }

    @Inject(method = "getVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    private void getVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        if (PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            if (soulSpeedValue <= 0) {
                cir.setReturnValue(super.getVelocityMultiplier());
            } else {
                cir.setReturnValue(1.0F);
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
        List<SetApugliEntityGroupPower> apugliGroups = PowerHolderComponent.getPowers(this, SetApugliEntityGroupPower.class);
        if(apugliGroups.size() > 0) {
            if(apugliGroups.size() > 1 || originsGroups.size() > 0) {
                Apugli.LOGGER.warn("Player " + this.getDisplayName().toString() + " has two or more instances of SetEntityGroupPower/SetApugliEntityGroupPower.");
            }
            cir.setReturnValue(apugliGroups.get(0).group);
        }
    }

    @Inject(method = "isUndead", at = @At("HEAD"), cancellable = true)
    private void isUndead(CallbackInfoReturnable<Boolean> cir) {
        if (this.getGroup() == ApugliEntityGroups.PLAYER_UNDEAD) {
            cir.setReturnValue(true);
        }
    }

    @Unique private int apugli_framesOnGround;
    @Unique private int apugli_velocityMultiplier;

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (PowerHolderComponent.getPowers(this, EdibleItemStackPower.class).size() != apugli_amountOfEdiblePower) {
            if ((LivingEntity)(Object)this instanceof PlayerEntity) {
                if (this.age % 10 == 0) {
                    for (int i = 0; i < ((PlayerEntityAccessor)this).getInventory().main.size(); i++) {
                        ItemStack itemStack = ((PlayerEntityAccessor)this).getInventory().main.get(i);
                        ItemStackFoodComponentAPI.removeFoodComponent(itemStack);
                    }
                    for (int i = 0; i < ((PlayerEntityAccessor)this).getInventory().armor.size(); i++) {
                        ItemStack armorStack = ((PlayerEntityAccessor)this).getInventory().getArmorStack(i);
                        ItemStackFoodComponentAPI.removeFoodComponent(armorStack);
                    }
                    ItemStack offHandStack = ((LivingEntity)(Object)this).getEquippedStack(EquipmentSlot.OFFHAND);
                    ItemStackFoodComponentAPI.removeFoodComponent(offHandStack);
                    apugli_amountOfEdiblePower = PowerHolderComponent.getPowers(this, EdibleItemStackPower.class).size();
                }
            }
        }
        PowerHolderComponent.getPowers(this, EdibleItemStackPower.class).forEach(EdibleItemStackPower::tempTick);
        if (PowerHolderComponent.hasPower(this, BunnyHopPower.class)) {
            if (this.onGround || this.isTouchingWater() || this.isInLava() || this.hasVehicle() || this.isFallFlying() || (this.getVelocity().getX() == 0 && this.getVelocity().getZ() == 0)) {
                this.apugli_setFramesOnGround();
            } else {
                this.apugli_framesOnGround = 0;
            }
            if (apugli_framesOnGround > 4) {
                this.apugli_velocityMultiplier = 0;
            }
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void travel(Vec3d movementInput, CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(this, BunnyHopPower.class)) {
            if (this.apugli_framesOnGround <= 4) {
                if (this.apugli_framesOnGround == 0) {
                    if (this.age % PowerHolderComponent.getPowers(this, BunnyHopPower.class).get(0).tickRate == 0) {
                        this.apugli_velocityMultiplier = (int)Math.min(apugli_velocityMultiplier + 1, PowerHolderComponent.getPowers(this, BunnyHopPower.class).get(0).maxVelocity  / PowerHolderComponent.getPowers(this, BunnyHopPower.class).get(0).increasePerTick);
                    }
                }
            }
            this.updateVelocity((float) PowerHolderComponent.getPowers(this, BunnyHopPower.class).get(0).increasePerTick * apugli_velocityMultiplier, movementInput);
        }
    }

    @Unique
    private void apugli_setFramesOnGround() {
        apugli_framesOnGround += 1;
    }

    @Unique
    public void addVelocityMultiplier(int value) {
        apugli_velocityMultiplier += value;
    }

    @Unique
    public int getApugliVelocityMultiplier() {
        return apugli_velocityMultiplier;
    }
}
