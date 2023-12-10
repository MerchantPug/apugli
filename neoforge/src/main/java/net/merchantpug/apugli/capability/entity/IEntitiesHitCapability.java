package net.merchantpug.apugli.capability.entity;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ProjectileEntityAccess;
import net.minecraft.resources.ResourceLocation;

public interface IEntitiesHitCapability extends ProjectileEntityAccess {
    ResourceLocation ID = Apugli.asResource("key_press");

    int getPowerValue(ResourceLocation value);

    void addToPowersThatHaveLanded(ResourceLocation value);

}
