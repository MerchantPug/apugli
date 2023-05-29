package net.merchantpug.apugli.action.factory.entity;

import com.mojang.math.Vector3f;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Space;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;

public class ClampedAddVelocityAction implements IActionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("x", SerializableDataTypes.FLOAT, 0.0F)
                .add("y", SerializableDataTypes.FLOAT, 0.0F)
                .add("z", SerializableDataTypes.FLOAT, 0.0F)
                .add("horizontal_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("horizontal_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("vertical_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("vertical_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("horizontal_clamp_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("horizontal_clamp_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("vertical_clamp_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("vertical_clamp_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("space", ApoliDataTypes.SPACE, Space.WORLD)
                .add("client", SerializableDataTypes.BOOLEAN, true)
                .add("server", SerializableDataTypes.BOOLEAN, true)
                .add("set", SerializableDataTypes.BOOLEAN, false);
    }

    @Override
    public void execute(SerializableData.Instance data, Entity entity) {
        if (entity instanceof Player
                && (entity.level.isClientSide ?
                !data.getBoolean("client") : !data.getBoolean("server")))
            return;
        Space space = data.get("space");
        Vector3f vec = new Vector3f(
                data.getFloat("x"),
                data.getFloat("y"),
                data.get("z"));
        space.toGlobal(vec, entity);
        vec.set((float) Services.PLATFORM.applyModifiers(entity, getModifiers(data, "horizontal_modifier"), vec.x()), (float) Services.PLATFORM.applyModifiers(entity, getModifiers(data, "vertical_modifier"), vec.y()), (float) Services.PLATFORM.applyModifiers(entity, getModifiers(data, "horizontal_modifier"), vec.z()));
        if (!data.getBoolean("set")) {
            vec.add((float) entity.getDeltaMovement().x(), (float) entity.getDeltaMovement().y(), (float) entity.getDeltaMovement().z());
        }
        entity.setDeltaMovement(
                clampModifiers(data, entity, vec.x(), "horizontal_clamp_modifier"),
                clampModifiers(data, entity, vec.y(), "vertical_clamp_modifier"),
                clampModifiers(data, entity, vec.z(), "horizontal_clamp_modifier"));
        entity.hurtMarked = true;
    }

    private float clampModifiers(SerializableData.Instance data, Entity entity, float original, String modifierKey) {
        if (!data.isPresent(modifierKey) && !data.isPresent(modifierKey + "s")) {
            return original;
        }
        return Mth.clamp(original, -Mth.abs((float) Services.PLATFORM.applyModifiers(entity, getModifiers(data, modifierKey), original)), Mth.abs((float) Services.PLATFORM.applyModifiers(entity, getModifiers(data, modifierKey), original)));
    }

    private <M> List<M> getModifiers(SerializableData.Instance data, String modifierKey) {
        return getModifiers(data, modifierKey, modifierKey + "s");
    }

    private <M> List<M> getModifiers(SerializableData.Instance data, String modifierKey, String modifiersKey) {
        List<M> modifiers = new ArrayList<>();
        data.<List<M>>ifPresent(modifiersKey, modifiers::addAll);
        data.<M>ifPresent(modifierKey, modifiers::add);
        return modifiers;
    }

}
