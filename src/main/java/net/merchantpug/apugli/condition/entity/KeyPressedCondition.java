package net.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.ApugliClient;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class KeyPressedCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (entity instanceof PlayerEntity player) {
            Active.Key key = data.get("key");
            KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);
            if (!component.getKeysToCheck().contains(key)) {
                component.addKeyToCheck(key);
                component.changePreviousKeysToCheckToCurrent();
            } else if (player.world.isClient && player instanceof ClientPlayerEntity) {
                ApugliClient.handleActiveKeys();
            }
            return component.getCurrentlyUsedKeys().contains(key);
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("key_pressed"), new SerializableData()
                .add("key", ApoliDataTypes.KEY),
                KeyPressedCondition::condition
        );
    }
}
