package com.github.merchantpug.apugli.mixin.client;

import io.github.apace100.origins.component.OriginComponent;
import com.github.merchantpug.apugli.powers.BunnyHopPower;
import com.github.merchantpug.apugli.powers.PreventShaderTogglePower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Shadow @Final private ResourceManager resourceContainer;

    @Unique
    private Identifier apugli$currentlyLoadedShader;

    @Inject(at = @At("TAIL"), method = "onCameraEntitySet")
    private void setCurrentlyLoadedShader(Entity entity, CallbackInfo ci) {
        OriginComponent.withPower(client.getCameraEntity(), PreventShaderTogglePower.class, null, shaderPower -> {
            Identifier shaderLoc = shaderPower.getShaderLocation();
            if(this.resourceContainer.containsResource(shaderLoc)) {
                apugli$currentlyLoadedShader = shaderLoc;
            }
        });
    }

    @Inject(at = @At("HEAD"), method = "render")
    private void cacheCurrentlyLoadedShader(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        OriginComponent.withPower(client.getCameraEntity(), PreventShaderTogglePower.class, null, shaderPower -> {
            Identifier shaderLoc = shaderPower.getShaderLocation();
            if(apugli$currentlyLoadedShader != shaderLoc) {
                if(this.resourceContainer.containsResource(shaderLoc)) {
                    apugli$currentlyLoadedShader = shaderLoc;
                }
            }
        });
        if(!OriginComponent.hasPower(client.getCameraEntity(), PreventShaderTogglePower.class) && apugli$currentlyLoadedShader != null) {
            apugli$currentlyLoadedShader = null;
        }
    }

    @Inject(at = @At("HEAD"), method = "toggleShadersEnabled", cancellable = true)
    private void disableShaderToggle(CallbackInfo ci) {
        OriginComponent.withPower(client.getCameraEntity(), PreventShaderTogglePower.class, null, shaderPower -> {
            Identifier shaderLoc = shaderPower.getShaderLocation();
            if(apugli$currentlyLoadedShader == shaderLoc) {
                ci.cancel();
            }
        });
    }
}
