package net.merchantpug.apugli.mixin.xplatform.client;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements ResourceManagerReloadListener, AutoCloseable {
    @Shadow
    @Final
    Minecraft minecraft;

    @ModifyVariable(method = "tickFov", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getFieldOfViewModifier()F"))
    private float modifyF(float f) {
        if (this.minecraft.getCameraEntity() instanceof LivingEntity living) {
            if(Services.POWER.hasPower(living, ApugliPowers.BUNNY_HOP.get())) {
                f += ApugliPowers.BUNNY_HOP.get().getDataFromPower(Services.POWER.getPowers(living, ApugliPowers.BUNNY_HOP.get()).get(0)).getFloat("increase_per_tick") * ApugliPowers.BUNNY_HOP.get().getValue(Services.POWER.getPowers(living, ApugliPowers.BUNNY_HOP.get()).get(0), living) * 20;
            }
        }
        return f;
    }

}
