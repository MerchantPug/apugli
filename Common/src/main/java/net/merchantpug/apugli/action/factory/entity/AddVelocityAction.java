package net.merchantpug.apugli.action.factory.entity;

import com.mojang.math.Vector3f;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Space;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class AddVelocityAction implements IActionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("x", SerializableDataTypes.FLOAT, 0.0F)
                .add("y", SerializableDataTypes.FLOAT, 0.0F)
                .add("z", SerializableDataTypes.FLOAT, 0.0F)
                .add("horizontal_modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                .add("horizontal_modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null)
                .add("vertical_modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                .add("vertical_modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null)
                .add("horizontal_post_modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                .add("horizontal_post_modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null)
                .add("vertical_post_modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                .add("vertical_post_modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null)
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
        vec.set(applyModifiers(data, vec.x(), "horizontal_modifier"),
                applyModifiers(data, vec.y(), "vertical_modifier"),
                applyModifiers(data, vec.z(), "horizontal_modifier"));
        if (!data.getBoolean("set")) {
            vec.add((float) entity.getDeltaMovement().x(), (float) entity.getDeltaMovement().y(), (float) entity.getDeltaMovement().z());
        }
        entity.setDeltaMovement(
                applyModifiers(data, vec.x(), "horizontal_post_modifier"),
                applyModifiers(data, vec.y(), "vertical_post_modifier"),
                applyModifiers(data, vec.z(), "horizontal_post_modifier"));
        entity.hurtMarked = true;
    }

    private float applyModifiers(SerializableData.Instance data, float original, String modifierKey) {
        if (!data.isPresent(modifierKey) && !data.isPresent(modifierKey + "s")) {
            return original;
        }
        return ((float) Services.PLATFORM.applyModifiers(getModifiers(data, modifierKey), original));
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
