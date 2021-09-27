package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class ApugliBiEntityConditions {
    public static void register() {
        register(new ConditionFactory<>(Apugli.identifier("rotation"), new SerializableData()
                .add("comparison", ApoliDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.DOUBLE),
                (data, pair) -> {
                    Vec3d vec3d = pair.getRight().getPos();
                    if (vec3d != null) {
                        Comparison comparison = (Comparison)data.get("comparison");
                        double compareTo = data.getDouble("compare_to");
                        Vec3d vec3d2 = pair.getLeft().getRotationVec(1.0F);
                        Vec3d vec3d3 = vec3d.relativize(pair.getLeft().getPos()).normalize();
                        vec3d3 = new Vec3d(vec3d3.x, 0.0D, vec3d3.z);
                        return comparison.compare(vec3d3.dotProduct(vec3d2), compareTo);
                    }
                    return false;
                }));
    }

    private static void register(ConditionFactory<Pair<Entity, Entity>> conditionFactory) {
        Registry.register(ApoliRegistries.BIENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
