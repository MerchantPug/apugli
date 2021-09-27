package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.SetEntityGroupPower;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.access.LivingEntityAccess;
import io.github.merchantpug.apugli.power.*;
import io.github.merchantpug.apugli.registry.ApugliEntityGroups;
import io.github.merchantpug.nibbles.ItemStackFoodComponentAPI;
import net.minecraft.block.pattern.CachedBlockPosition;
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

    @Shadow protected abstract boolean isOnSoulSpeedBlock();

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

    @Inject(method = "isOnSoulSpeedBlock", at = @At("HEAD"), cancellable = true)
    private void isOnSoulSpeedBlock(CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(this, ModifySoulSpeedPower.class).forEach(power -> {
            if (power.blockCondition != null) {
                cir.setReturnValue(power.blockCondition.test(new CachedBlockPosition(this.world, this.getVelocityAffectingPos(), true)));
            }
        });
    }

    @ModifyVariable(method = "addSoulSpeedBoostIfNeeded", at = @At("STORE"), ordinal = 0)
    private int replaceLevelOfSouLSpeed(int i) {
        return i = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, i);
    }

    @Inject(method = "getVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    private void getVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        if (PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            if (soulSpeedValue <= 0 || !this.isOnSoulSpeedBlock()) {
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

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (PowerHolderComponent.getPowers(this, EdibleItemPower.class).size() != apugli_amountOfEdiblePower) {
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
                    apugli_amountOfEdiblePower = PowerHolderComponent.getPowers(this, EdibleItemPower.class).size();
                }
            }
        }
        PowerHolderComponent.getPowers(this, EdibleItemPower.class).forEach(EdibleItemPower::tempTick);
        if (PowerHolderComponent.hasPower(this, BunnyHopPower.class)) {
            BunnyHopPower bunnyHopPower = PowerHolderComponent.getPowers(this, BunnyHopPower.class).get(0);
            if (apugli_framesOnGround > 4) {
                bunnyHopPower.setValue(0);
            }
            if (this.onGround || this.isTouchingWater() || this.isInLava() || this.hasVehicle() || this.isFallFlying() || (this.getVelocity().getX() == 0 && this.getVelocity().getZ() == 0)) {
                if (apugli_framesOnGround <= 4) {
                    apugli_framesOnGround += 1;
                }
            } else {
                this.apugli_framesOnGround = 0;
            }
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void travel(Vec3d movementInput, CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(this, BunnyHopPower.class)) {
            BunnyHopPower bunnyHopPower = PowerHolderComponent.getPowers(this, BunnyHopPower.class).get(0);
            if (this.apugli_framesOnGround <= 4) {
                if (this.apugli_framesOnGround == 0) {
                    if (this.age % bunnyHopPower.tickRate == 0) {
                        if (bunnyHopPower.getValue() != bunnyHopPower.getMax()) {
                            bunnyHopPower.increment();
                        }
                    }
                }
            }
            this.updateVelocity((float)bunnyHopPower.increasePerTick * bunnyHopPower.getValue(), movementInput);
        }
    }
}
