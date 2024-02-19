package net.merchantpug.apugli.mixin.forge.integration.pehkui;

import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.pehkui.api.ScaleModifier;

@Pseudo
@Mixin(ScaleModifier.class)
public abstract class ScaleModifierMixin {

    @Shadow(remap = false) public abstract float getPriority();

    @Inject(method = "compareTo(Lvirtuoel/pehkui/api/ScaleModifier;)I", at = @At("RETURN"), remap = false, cancellable = true)
    private void apugli$cancelCompareTo(ScaleModifier o, CallbackInfoReturnable<Integer> cir) {
        if (o instanceof ApoliScaleModifier<?>)
            cir.setReturnValue(Float.compare(o.getPriority(), getPriority()));
    }
}
