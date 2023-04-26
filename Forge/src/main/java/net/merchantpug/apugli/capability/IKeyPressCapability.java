package net.merchantpug.apugli.capability;

import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import net.merchantpug.apugli.Apugli;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

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