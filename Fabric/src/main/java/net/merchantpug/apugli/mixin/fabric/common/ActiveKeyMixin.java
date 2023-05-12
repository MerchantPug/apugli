package net.merchantpug.apugli.mixin.fabric.common;

import io.github.apace100.apoli.power.Active;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(value = Active.Key.class, remap = false)
public class ActiveKeyMixin {
    @Shadow public String key;
    @Shadow public boolean continuous;

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Active.Key otherKey))
            return false;

        return otherKey.key.equals(this.key) && otherKey.continuous == this.continuous;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.continuous);
    }
}