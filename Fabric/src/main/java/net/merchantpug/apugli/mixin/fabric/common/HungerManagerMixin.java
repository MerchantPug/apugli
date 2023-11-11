package net.merchantpug.apugli.mixin.fabric.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(FoodData.class)
public abstract class HungerManagerMixin {
    @Shadow
    public abstract void eat(int food, float saturationModifier);

    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private void apugli$eat(Item item, ItemStack stack, CallbackInfo ci) {
        if (Services.PLATFORM.getEntityFromItemStack(stack) instanceof LivingEntity living) {
            Optional<EdibleItemPower> power = Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(living.level(), stack)).findFirst();
            power.ifPresent(p -> {
                this.eat(p.getFoodComponent().getNutrition(), p.getFoodComponent().getSaturationModifier());
                ci.cancel();
            });
        }
    }
}