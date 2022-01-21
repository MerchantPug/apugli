package io.github.merchantpug.apugli.mixin.client;

import io.github.apace100.origins.component.OriginComponent;
import io.github.merchantpug.apugli.powers.BunnyHopPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.SynchronousResourceReloadListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements SynchronousResourceReloadListener, AutoCloseable {
    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyVariable(method = "updateMovementFovMultiplier", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSpeed()F"))
    private float modifyF(float f) {
        if (OriginComponent.hasPower(this.client.getCameraEntity(), BunnyHopPower.class)) {
            f += OriginComponent.getPowers(this.client.getCameraEntity(), BunnyHopPower.class).get(0).increasePerTick * OriginComponent.getPowers(this.client.getCameraEntity(), BunnyHopPower.class).get(0).getValue() * 20;
        }
        return f;
    }
}
