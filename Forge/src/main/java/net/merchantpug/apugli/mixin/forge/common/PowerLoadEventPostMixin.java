package net.merchantpug.apugli.mixin.forge.common;

import io.github.apace100.apoli.integration.PowerLoadEvent;
import net.merchantpug.apugli.access.PowerLoadEventPostAccess;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PowerLoadEvent.Post.class)
@Implements(@Interface(iface = PowerLoadEventPostAccess.class, prefix = "apugli$"))
public class PowerLoadEventPostMixin {
    @Unique
    private ResourceLocation apugli$fixedPowerId;

    public ResourceLocation apugli$getFixedId() {
        return apugli$fixedPowerId;
    }

    public void apugli$setFixedId(ResourceLocation value) {
        apugli$fixedPowerId = value;
    }

}
