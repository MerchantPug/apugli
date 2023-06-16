package net.merchantpug.apugli.access;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ExplosionAccess {
    void setExplosionDamageModifiers(List<AttributeModifier> value);
    List<AttributeModifier> getExplosionDamageModifiers();

    void setExplosionKnockbackModifiers(List<AttributeModifier> value);
    List<AttributeModifier> getExplosionKnockbackModifiers();

    <M> void setBiEntityPredicate(@Nullable M value);
    @Nullable Object getBiEntityPredicate();

    void setExplosionVolumeModifiers(List<AttributeModifier> value);
    List<AttributeModifier> getExplosionVolumeModifiers();

    void setExplosionPitchModifiers(List<AttributeModifier> value);
    List<AttributeModifier> getExplosionPitchModifiers();
}
