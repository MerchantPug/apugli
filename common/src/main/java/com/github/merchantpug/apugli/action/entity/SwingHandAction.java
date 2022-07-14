package com.github.merchantpug.apugli.action.entity;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.util.BackportedDataTypes;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class SwingHandAction {

    public static void action(SerializableData.Instance data, Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            living.swingHand((Hand) data.get("hand"));
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("swing_hand"),
            new SerializableData()
                .add("hand", BackportedDataTypes.HAND, Hand.MAIN_HAND),
            SwingHandAction::action
        );
    }
}