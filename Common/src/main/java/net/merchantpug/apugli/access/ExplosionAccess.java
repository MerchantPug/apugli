package net.merchantpug.apugli.access;

import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public interface ExplosionAccess {
    void setRocketJump(boolean value);
    boolean isRocketJump();

    void setExplosionDamageModifiers(List<?> value);
    void setBiEntityPredicate(@Nullable Predicate<Tuple<Entity, Entity>> value);
}
