package net.merchantpug.apugli.mixin.xplatform.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements ResourceManagerReloadListener, AutoCloseable {
    @Shadow
    @Final
    private Minecraft client;

    @ModifyVariable(method = "updateFovMultiplier", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getFovMultiplier()F"))
    private float modifyF(float f) {
        if(PowerHolderComponent.hasPower(this.client.getCameraEntity(), BunnyHopPower.class)) {
            f += PowerHolderComponent.getPowers(this.client.getCameraEntity(), BunnyHopPower.class).get(0).increasePerTick * PowerHolderComponent.getPowers(this.client.getCameraEntity(), BunnyHopPower.class).get(0).getValue() * 20;
        }
        return f;
    }
}
