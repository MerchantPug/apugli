package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.BunnyHopPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.SynchronousResourceReloader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements SynchronousResourceReloader, AutoCloseable {
    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyVariable(method = "updateMovementFovMultiplier", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSpeed()F"))
    private float modifyF(float f) {
        if (PowerHolderComponent.hasPower(this.client.getCameraEntity(), BunnyHopPower.class)) {
            f += PowerHolderComponent.getPowers(this.client.getCameraEntity(), BunnyHopPower.class).get(0).increasePerTick * PowerHolderComponent.getPowers(this.client.getCameraEntity(), BunnyHopPower.class).get(0).getValue() * 20;
        }
        return f;
    }
}
