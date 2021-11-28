package io.github.merchantpug.apugli.util;

import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.Collections;
import java.util.List;

public class ApugliAttributeUtil {
    public static double inverseModifiers(List<EntityAttributeModifier> modifiers, double baseValue, double originalBaseValue) {
        double currentValue = baseValue;
        if(modifiers != null) {
            Collections.reverse(modifiers);
            for(EntityAttributeModifier modifier : modifiers) {
                switch (modifier.getOperation()) {
                    case ADDITION -> currentValue -= modifier.getValue();
                    case MULTIPLY_BASE -> currentValue -= originalBaseValue * modifier.getValue();
                    case MULTIPLY_TOTAL -> currentValue /= (1 + modifier.getValue());
                }
            }
        }
        return currentValue;
    }
}
