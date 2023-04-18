<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/client/GameRendererMixin.java
package net.merchantpug.apugli.mixin.client;

import net.merchantpug.apugli.power.BunnyHopPower;
========
package com.github.merchantpug.apugli.mixin.xplatforn.client;

import the.great.migration.merchantpug.apugli.power.BunnyHopPower;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/client/GameRendererMixin.java
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
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
