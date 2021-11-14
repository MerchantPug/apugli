package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.ActionOnEquipPower;
import io.github.merchantpug.apugli.power.CustomFootstepPower;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        List<CustomFootstepPower> powers = PowerHolderComponent.getPowers((PlayerEntity)(Object)this, CustomFootstepPower.class);
        if (powers.stream().anyMatch(CustomFootstepPower::isMuted)) return;
        super.playStepSound(pos, state);
        if (powers.isEmpty()) return;
        powers.forEach(power -> power.playFootstep(this));
    }

    @Inject(method = "equipStack", at = @At(value = "TAIL"))
    public void equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if (slot.getType() != EquipmentSlot.Type.ARMOR && !slot.equals(EquipmentSlot.OFFHAND)) return;

        PowerHolderComponent.getPowers((PlayerEntity)(Object)this, ActionOnEquipPower.class).forEach(power -> power.fireAction(slot, stack));
    }
}
