package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class AttackComponentImpl implements AttackComponent, AutoSyncedComponent, ServerTickingComponent {
    @Nullable private Integer attackerId;
    @Nullable private Integer attackingId;
    private final LivingEntity provider;

    public AttackComponentImpl(LivingEntity provider) {
        this.provider = provider;
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == provider || PlayerLookup.tracking(provider).contains(player);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {

    }

    @Override
    public void writeToNbt(NbtCompound tag) {

    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeBoolean(attackerId != null);
        if (attackerId != null) {
            buf.writeInt(attackerId);
        }
        buf.writeBoolean(attackingId != null);
        if (attackingId != null) {
            buf.writeInt(attackingId);
        }
    }

    public void applySyncPacket(PacketByteBuf buf) {
        if (buf.readBoolean()) {
            attackerId = buf.readInt();
        }
        if (buf.readBoolean()) {
            attackingId = buf.readInt();
        }
    }

    @Override
    public void setAttacker(@Nullable Entity entity) {
        attackerId = entity != null ? entity.getId() : null;
    }

    @Override
    public void setAttacking(@Nullable Entity entity) {
        attackingId = entity != null ? entity.getId() : null;
    }

    @Override
    public @Nullable Integer getAttacker() {
        return attackerId;
    }

    @Override
    public @Nullable Integer getAttacking() {
        return attackingId;
    }


    @Override
    public void serverTick() {
        boolean hasChanged = false;
        if (attackerId != null) {
            Entity attacker = provider.world.getEntityById(attackerId);
            if (attacker == null || !PlayerLookup.tracking(attacker).contains(provider) || !attacker.isAlive()) {
                attackerId = null;
                hasChanged = true;
            }
        }
        if (attackingId != null) {
            Entity attacking = provider.world.getEntityById(attackingId);
            if (attacking == null || !PlayerLookup.tracking(attacking).contains(provider) || !attacking.isAlive()) {
                attackingId = null;
                hasChanged = true;
            }
        }
        if (hasChanged) {
            ApugliEntityComponents.ATTACK_COMPONENT.sync(provider);
        }
    }
}
