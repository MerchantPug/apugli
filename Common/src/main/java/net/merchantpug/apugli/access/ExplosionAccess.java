package net.merchantpug.apugli.access;

import javax.annotation.Nullable;
import java.util.List;

public interface ExplosionAccess {
    void setRocketJump(boolean value);
    boolean isRocketJump();

    void setExplosionDamageModifiers(List<?> value);
    <C> void setBiEntityPredicate(@Nullable C value);
}
