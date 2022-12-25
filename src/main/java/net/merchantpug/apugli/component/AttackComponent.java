package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface AttackComponent extends ServerTickingComponent {
    @Nullable Entity getAttacker();
    @Nullable Entity getAttacking();
}
