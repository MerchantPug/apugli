package net.merchantpug.apugli.mixin.client;

import net.merchantpug.apugli.power.PlayerModelTypePower;
import net.merchantpug.apugli.power.SetTexturePower;
import com.mojang.authlib.GameProfile;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
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

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void getModel(CallbackInfoReturnable<String> cir) {
        List<PlayerModelTypePower> playerModelTypePowers = PowerHolderComponent.getPowers(this, PlayerModelTypePower.class);
        List<SetTexturePower> setTexturePowers = PowerHolderComponent.getPowers(this, SetTexturePower.class).stream().filter(power -> power.model != null).toList();
        if (playerModelTypePowers.size() + setTexturePowers.size() > 1) {
            Apoli.LOGGER.warn("Entity " + this.getDisplayName().toString() + " has two instances of player model setting powers active.");
        }
        if (playerModelTypePowers.size() > 0) {
            cir.setReturnValue(PowerHolderComponent.getPowers(this, PlayerModelTypePower.class).get(0).model.toString());
        } else if (setTexturePowers.size() > 0) {
            cir.setReturnValue(PowerHolderComponent.getPowers(this, SetTexturePower.class).get(0).model.toString());
        }
    }
}
