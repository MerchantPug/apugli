package net.merchantpug.apugli.access;

import io.github.apace100.apoli.util.modifier.Modifier;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public interface ExplosionAccess {
    void setRocketJump(boolean value);
    boolean isRocketJump();

    void setExplosionDamageModifiers(List<Modifier> value);
    void setBiEntityPredicate(@Nullable Predicate<Pair<Entity, Entity>> value);
}
