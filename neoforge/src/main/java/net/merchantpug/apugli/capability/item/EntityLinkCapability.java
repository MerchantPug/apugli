package net.merchantpug.apugli.capability.item;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityLinkCapability implements IEntityLinkCapability, ICapabilityProvider {

    ItemStack provider;
    Entity entity;

    public static final Capability<EntityLinkCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<EntityLinkCapability> thisOptional = LazyOptional.of(() -> this);

    public EntityLinkCapability(ItemStack provider) {
        this.provider = provider;
    }

    @Override
    public void setEntity(@Nullable Entity entity) {
        this.entity = entity;
    }

    @Override
    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return EntityLinkCapability.INSTANCE.orEmpty(cap, thisOptional);
    }
}