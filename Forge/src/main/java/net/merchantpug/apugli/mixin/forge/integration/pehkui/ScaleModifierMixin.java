package net.merchantpug.apugli.mixin.forge.integration.pehkui;

import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import virtuoel.pehkui.api.ScaleModifier;

@Pseudo
@Mixin(ScaleModifier.class)
public class ScaleModifierMixin {
    @Inject(method = "compareTo(Lvirtuoel/pehkui/api/ScaleModifier;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;compareTo(Lnet/minecraft/resources/ResourceLocation;)I"), cancellable = true, remap = false)
    private void apugli$cancelCompareTo(ScaleModifier o, CallbackInfoReturnable<Integer> cir) {
        if (o instanceof ApoliScaleModifier)
            cir.setReturnValue(1);
    }
}
