package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface AttackComponent extends Component {
    void setAttacker(@Nullable Entity entity);
    void setAttacking(@Nullable Entity entity);
    @Nullable Integer getAttacker();
    @Nullable Integer getAttacking();
}
