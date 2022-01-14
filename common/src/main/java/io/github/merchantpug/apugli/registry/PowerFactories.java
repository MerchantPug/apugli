package io.github.merchantpug.apugli.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.merchantpug.apugli.powers.ActionOnBlockPlacedPower;
import io.github.merchantpug.apugli.powers.ModifyBlockPlacedPower;

@SuppressWarnings({"unchecked", "UnstableApiUsage", "deprecation"})
public class PowerFactories {
    public static void register() {
        register(ActionOnBlockPlacedPower.getFactory());
        register(ModifyBlockPlacedPower.getFactory());
    }

    @ExpectPlatform
    private static void register(PowerFactory<?> serializer) {
        throw new AssertionError();
    }
}
