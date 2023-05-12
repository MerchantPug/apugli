package net.merchantpug.apugli.access;

import net.minecraft.resources.ResourceLocation;
import io.github.edwinmindcraft.apoli.common.power.MultiplePower;

/**
 This exists due to a bug with {@link MultiplePower} powers where there is no `_` separating the multiple power id and the child power id.
 */
public interface PowerLoadEventPostAccess {
    ResourceLocation getFixedId();
    void setFixedId(ResourceLocation value);
}
