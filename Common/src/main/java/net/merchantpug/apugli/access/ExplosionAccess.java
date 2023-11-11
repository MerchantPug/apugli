package net.merchantpug.apugli.access;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ExplosionAccess {
    void apugli$setExplosionDamageModifiers(List<?> value);
    List<?> apugli$getExplosionDamageModifiers();

    void apugli$setExplosionKnockbackModifiers(List<?> value);
    List<?> apugli$getExplosionKnockbackModifiers();

    <M> void apugli$setBiEntityPredicate(@Nullable M value);
    @Nullable Object apugli$getBiEntityPredicate();

    void apugli$setExplosionVolumeModifiers(List<?> value);
    List<?> apugli$getExplosionVolumeModifiers();

    void apugli$setExplosionPitchModifiers(List<?> value);
    List<?> apugli$getExplosionPitchModifiers();
}
