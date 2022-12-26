package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class AttackComponentImpl implements AttackComponent, AutoSyncedComponent, ServerTickingComponent {
    @Nullable private Entity attacker = null;
    @Nullable private Entity attacking = null;
    @Nullable private Entity previousAttacking = null;
    @Nullable private Entity previousAttacker = null;
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
        buf.writeBoolean(attacker != previousAttacker);
        if (attacker != previousAttacker) {
            buf.writeBoolean(attacker != null);
            if (attacker != null) {
                buf.writeInt(attacker.getId());
            }
        }
        buf.writeBoolean(attacking != previousAttacking);
        if (attacking != previousAttacking) {
            buf.writeBoolean(attacking != null);
            if (attacking != null) {
                buf.writeInt(attacking.getId());
            }
        }
    }

    public void applySyncPacket(PacketByteBuf buf) {
        boolean updateAttacker = buf.readBoolean();
        if (updateAttacker) {
            boolean attackerNotNull = buf.readBoolean();
            if (attackerNotNull)
                this.attacker = provider.world.getEntityById(buf.readInt());
            else
                this.attacker = null;
        }
        boolean updateAttacking = buf.readBoolean();
        if (updateAttacking) {
            boolean attackingNotNull = buf.readBoolean();
            if (attackingNotNull)
                this.attacking = provider.world.getEntityById(buf.readInt());
            else
                this.attacking = null;
        }
    }

    @Override
    public @Nullable Entity getAttacker() {
        return attacker;
    }

    @Override
    public @Nullable Entity getAttacking() {
        return attacking;
    }


    @Override
    public void serverTick() {
        previousAttacker = attacker;
        previousAttacking = attacking;

        boolean hasChanged = false;
        if (provider.getAttacker() != attacker) {
            attacker = provider.getAttacker();
            hasChanged = true;
        }

        if (provider.getAttacking() != attacking) {
            attacking = provider.getAttacking();
            hasChanged = true;
        }

        if (hasChanged) {
            ApugliEntityComponents.ATTACK_COMPONENT.sync(provider);
        }
    }
}
