package net.merchantpug.apugli.registry.condition;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.condition.factory.item.BaseEnchantmentCondition;
import net.merchantpug.apugli.condition.factory.item.OnCooldownCondition;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.item.ItemStack;

public class ApugliItemConditions {
    
    public static void registerAll() {
        register("base_enchantment", new BaseEnchantmentCondition());
        register("on_cooldown", new OnCooldownCondition());
    }
    
    private static void register(String name, IConditionFactory<ItemStack> action) {
        Services.CONDITION.registerItem(name, action);
    }
    
}
