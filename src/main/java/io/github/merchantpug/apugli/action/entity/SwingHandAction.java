package io.github.merchantpug.apugli.action.entity;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class SwingHandAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if (entity instanceof PlayerEntity && !entity.world.isClient) {
            ((PlayerEntity) entity).swingHand((Hand)data.get("hand"), true);
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("swing_hand"), new SerializableData()
                .add("hand", SerializableDataTypes.HAND),
                SwingHandAction::action
        );
    }
}
