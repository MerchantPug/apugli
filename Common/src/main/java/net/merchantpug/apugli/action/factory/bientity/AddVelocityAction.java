package net.merchantpug.apugli.action.factory.bientity;

import io.github.apace100.apoli.util.Space;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class AddVelocityAction implements IActionFactory<Tuple<Entity, Entity>> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("x", SerializableDataTypes.FLOAT, 0F)
                .add("y", SerializableDataTypes.FLOAT, 0F)
                .add("z", SerializableDataTypes.FLOAT, 0F)
                .add("horizontal_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("horizontal_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("vertical_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("vertical_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("horizontal_post_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("horizontal_post_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("vertical_post_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("vertical_post_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("client", SerializableDataTypes.BOOLEAN, true)
                .add("server", SerializableDataTypes.BOOLEAN, true)
                .add("set", SerializableDataTypes.BOOLEAN, false)
                .add("reference", SerializableDataType.enumValue(Reference.class), Reference.POSITION);
    }

    @Override
    public void execute(SerializableData.Instance data, Tuple<Entity, Entity> pair) {

        Entity actor = pair.getA();
        Entity target = pair.getB();

        if ((actor == null || target == null) || (target instanceof Player && (target.level().isClientSide() ? !data.getBoolean("client") : !data.getBoolean("server")))) {
            return;
        }

        Vector3f vec = new Vector3f(data.getFloat("x"), data.getFloat("y"), data.getFloat("z"));
        vec.set(applyModifiers(data, actor, vec.x(), "horizontal_modifier"),
                applyModifiers(data, actor, vec.y(), "vertical_modifier"),
                applyModifiers(data, actor, vec.z(), "horizontal_modifier"));
        TriConsumer<Float, Float, Float> method = data.getBoolean("set") ? target::setDeltaMovement : (x, y, z) -> target.setDeltaMovement(target.getDeltaMovement().add(x, y, z));

        Reference reference = data.get("reference");
        Vec3 refVec = reference.apply(actor, target);

        Space.transformVectorToBase(refVec, vec, actor.getYRot(), true); // vector normalized by method
        method.accept(
                applyModifiers(data, actor, vec.x(), "horizontal_post_modifier"),
                applyModifiers(data, actor, vec.y(), "vertical_post_modifier"),
                applyModifiers(data, actor, vec.z(), "horizontal_post_modifier"));

        target.hurtMarked = true;

    }

    private float applyModifiers(SerializableData.Instance data, Entity entity, float original, String modifierKey) {
        if (!data.isPresent(modifierKey) && !data.isPresent(modifierKey + "s")) {
            return original;
        }
        return ((float) Services.PLATFORM.applyModifiers(entity, getModifiers(data, modifierKey), original));
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

    public enum Reference {

        POSITION((actor, target) -> target.position().subtract(actor.position())),
        ROTATION((actor, target) -> {

            float pitch = actor.getXRot();
            float yaw = actor.getYRot();

            float i = 0.017453292F;

            float j = -Mth.sin(yaw * i) * Mth.cos(pitch * i);
            float k = -Mth.sin(pitch * i);
            float l =  Mth.cos(yaw * i) * Mth.cos(pitch * i);

            return new Vec3(j, k, l);

        });

        final BiFunction<Entity, Entity, Vec3> refFunction;
        Reference(BiFunction<Entity, Entity, Vec3> refFunction) {
            this.refFunction = refFunction;
        }

        public Vec3 apply(Entity actor, Entity target) {
            return refFunction.apply(actor, target);
        }

    }

}
