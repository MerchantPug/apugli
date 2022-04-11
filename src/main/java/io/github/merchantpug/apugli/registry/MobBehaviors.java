package io.github.merchantpug.apugli.registry;

import io.github.merchantpug.apugli.behavior.BehaviorFactory;
import io.github.merchantpug.apugli.behavior.types.FleeMobBehavior;
import io.github.merchantpug.apugli.behavior.types.PassiveMobBehavior;
import net.minecraft.util.registry.Registry;

public class MobBehaviors {
    public static void register() {
        register(FleeMobBehavior.getFactory());
        register(PassiveMobBehavior.getFactory());
    }

    private static void register(BehaviorFactory<?> serializer) {
        Registry.register(ApugliRegistries.BEHAVIOR_FACTORIES, serializer.getSerializerId(), serializer);
    }
}