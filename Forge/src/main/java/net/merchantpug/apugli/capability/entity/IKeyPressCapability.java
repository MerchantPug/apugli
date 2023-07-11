package net.merchantpug.apugli.capability.entity;

import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import net.merchantpug.apugli.Apugli;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public interface IKeyPressCapability {
    ResourceLocation ID = Apugli.asResource("key_press");

    Set<IActivePower.Key> getCurrentlyUsedKeys();
    Set<IActivePower.Key> getPreviouslyUsedKeys();
    void setPreviouslyUsedKeys();

    Set<IActivePower.Key> getKeysToCheck();
    Set<IActivePower.Key> getPreviousKeysToCheck();
    void setPreviousKeysToCheck();

    void addKeyToCheck(IActivePower.Key key);
    void changePreviousKeysToCheckToCurrent();
    void addKey(IActivePower.Key key);
    void removeKey(IActivePower.Key key);
}