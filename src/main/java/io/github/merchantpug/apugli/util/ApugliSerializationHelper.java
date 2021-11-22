package io.github.merchantpug.apugli.util;

import io.github.apace100.calio.SerializationHelper;
import io.github.apace100.calio.util.StatusEffectChance;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.item.FoodComponent;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

public class ApugliSerializationHelper {
    public static void writeFoodComponent(PacketByteBuf buf, FoodComponent foodComponent) {
        buf.writeInt(foodComponent.getHunger());
        buf.writeFloat(foodComponent.getSaturationModifier());
        buf.writeBoolean(foodComponent.isMeat());
        buf.writeBoolean(foodComponent.isAlwaysEdible());
        buf.writeBoolean(foodComponent.isSnack());
        buf.writeInt(foodComponent.getStatusEffects().size());
        for (int i = 0; i < foodComponent.getStatusEffects().size(); i++) {
            SerializationHelper.writeStatusEffect(buf, foodComponent.getStatusEffects().get(i).getFirst());
            buf.writeFloat(foodComponent.getStatusEffects().get(i).getSecond());
        }
    }

    public static FoodComponent readFoodComponent(PacketByteBuf buf) {
        FoodComponent.Builder builder = new FoodComponent.Builder().hunger(buf.readInt()).saturationModifier(buf.readFloat());
        boolean meat = buf.readBoolean();
        boolean alwaysEdible = buf.readBoolean();
        boolean snack = buf.readBoolean();
        int amountOfStatusEffects = buf.readInt();
        List<StatusEffectChance> chanceList = new ArrayList<>();
        if (meat) {
            builder.meat();
        }
        if (alwaysEdible) {
            builder.alwaysEdible();
        }
        if (snack) {
            builder.snack();
        }
        for (int i = 0; i < amountOfStatusEffects; i++) {
            StatusEffectChance statusEffectChance = new StatusEffectChance();
            statusEffectChance.statusEffectInstance = SerializationHelper.readStatusEffect(buf);
            statusEffectChance.chance = buf.readFloat();
            chanceList.add(statusEffectChance);
        }
        chanceList.forEach(sec -> {
            builder.statusEffect(sec.statusEffectInstance, sec.chance);
        });
        Apugli.LOGGER.info(builder.build().toString());
        return builder.build();
    }
}
