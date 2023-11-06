package net.merchantpug.apugli.mixin.forge.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @ModifyExpressionValue(method = "Lnet/minecraft/client/renderer/GameRenderer;lambda$pick$61(Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPickable()Z"))
    private static boolean preventPickingOfPreventedEntities(boolean original, Entity entity) {
        return original && Services.POWER.getPowers(Minecraft.getInstance().player, ApugliPowers.PREVENT_ENTITY_SELECTION.get()).stream().noneMatch(p -> p.shouldPrevent(entity));
    }
}
