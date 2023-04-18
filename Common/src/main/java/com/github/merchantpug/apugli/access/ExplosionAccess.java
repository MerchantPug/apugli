package com.github.merchantpug.apugli.access;

import io.github.apace100.apoli.util.modifier.Modifier;

import java.util.List;

public interface ExplosionAccess {
    void setRocketJump(boolean value);
    boolean isRocketJump();

    void setExplosionDamageModifiers(List<Modifier> value);
}
