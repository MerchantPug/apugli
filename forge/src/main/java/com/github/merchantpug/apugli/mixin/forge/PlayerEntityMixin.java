package com.github.merchantpug.apugli.mixin.forge;

import com.github.merchantpug.apugli.powers.AerialAffinityPower;
import io.github.apace100.origins.component.OriginComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "getDigSpeed", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;onGround:Z", opcode = Opcodes.GETFIELD), remap = false)
    private boolean hasAirAffinity(PlayerEntity instance) {
        return OriginComponent.hasPower(instance, AerialAffinityPower.class) || instance.isOnGround();
    }
}