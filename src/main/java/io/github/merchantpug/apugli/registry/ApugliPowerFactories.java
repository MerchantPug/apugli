package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.merchantpug.apugli.power.*;
import net.minecraft.util.registry.Registry;

public class ApugliPowerFactories {
    public static void register() {
        register(BunnyHopPower.getFactory());
        register(EdibleItemPower.getFactory());
        register(EffectWhitelistPower.getFactory());
        register(EnergySwirlPower.getFactory());
        register(ModifyEquippedItemRenderPower.getFactory());
        register(ModifySoulSpeedPower.getFactory());
        register(RocketJumpPower.getFactory());
        register(SetApugliEntityGroupPower.getFactory());
        register(SetTexturePower.getFactory());
    }

    public static void register(PowerFactory<?> serializer) {
        Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }
}
