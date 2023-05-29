package net.merchantpug.apugli.access;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ExplosionAccess {
    void setExplosionDamageModifiers(List<?> value);
    List<?> getExplosionDamageModifiers();

    void setExplosionKnockbackModifiers(List<?> value);
    List<?> getExplosionKnockbackModifiers();

    <M> void setBiEntityPredicate(@Nullable M value);
    @Nullable Object getBiEntityPredicate();

    void setExplosionVolumeModifiers(List<?> value);
    List<?> getExplosionVolumeModifiers();

    void setExplosionPitchModifiers(List<?> value);
    List<?> getExplosionPitchModifiers();
}
