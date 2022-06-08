package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.power.SetTexturePower;
import com.mojang.authlib.GameProfile;
import io.github.apace100.apoli.component.PowerHolderComponent;
import com.github.merchantpug.apugli.util.PlayerModelType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void getModel(CallbackInfoReturnable<String> cir) {
        if (PowerHolderComponent.hasPower(this, SetTexturePower.class) && (PowerHolderComponent.getPowers(this, SetTexturePower.class).get(0).model.equals(PlayerModelType.DEFAULT) || PowerHolderComponent.getPowers(this, SetTexturePower.class).get(0).model.equals(PlayerModelType.SLIM))) {
            cir.setReturnValue(PowerHolderComponent.getPowers(this, SetTexturePower.class).get(0).model.toString());
        }
    }
}
