package io.github.merchantpug.apugli.registry;

import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.behavior.BehaviorFactory;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;

public class ApugliRegistries {
    public static final Registry<BehaviorFactory> BEHAVIOR_FACTORIES;

    static {
        BEHAVIOR_FACTORIES = FabricRegistryBuilder.createSimple(BehaviorFactory.class, Apugli.identifier("behavior_factory")).buildAndRegister();
    }
}