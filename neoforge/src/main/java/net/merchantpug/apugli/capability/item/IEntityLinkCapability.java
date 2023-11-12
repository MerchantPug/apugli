package net.merchantpug.apugli.capability.item;

import net.merchantpug.apugli.Apugli;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public interface IEntityLinkCapability {
    ResourceLocation ID = Apugli.asResource("entity_link");

    void setEntity(@Nullable Entity entity);
    @Nullable Entity getEntity();

}